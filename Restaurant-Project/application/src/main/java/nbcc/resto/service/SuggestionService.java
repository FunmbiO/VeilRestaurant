package nbcc.resto.service;

import nbcc.auth.entity.UserLoginEntity;
import nbcc.auth.repository.UserLoginJPARepository;
import nbcc.resto.dto.SuggestionDTO;
import nbcc.resto.entity.Suggestion;
import nbcc.resto.entity.Suggestion.Priority;
import nbcc.resto.entity.Suggestion.Status;
import nbcc.resto.entity.Suggestion.TargetType;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.exception.MenuNotFoundException;
import nbcc.resto.repository.EventRepository;
import nbcc.resto.repository.MenuRepository;
import nbcc.resto.repository.SuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final EventRepository eventRepository;
    private final MenuRepository menuRepository;
    private final UserLoginJPARepository userRepository;

    public SuggestionService(SuggestionRepository suggestionRepository,
                             EventRepository eventRepository,
                             MenuRepository menuRepository,
                             UserLoginJPARepository userRepository) {
        this.suggestionRepository = suggestionRepository;
        this.eventRepository      = eventRepository;
        this.menuRepository       = menuRepository;
        this.userRepository       = userRepository;
    }

    private String resolveUsername(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .map(UserLoginEntity::getUsername)
                .orElse("[Deleted User]");
    }

    private String resolveTargetName(TargetType type, Long targetId) {
        if (type == TargetType.EVENT) {
            return eventRepository.findById(targetId)
                    .map(e -> e.getName()).orElse("[Deleted Event]");
        } else {
            return menuRepository.findById(targetId)
                    .map(m -> m.getName()).orElse("[Deleted Menu]");
        }
    }

    private SuggestionDTO toDTO(Suggestion s) {
        SuggestionDTO dto = SuggestionDTO.from(s);
        dto.setCreatedByUsername(resolveUsername(s.getCreatedBy()));
        dto.setTargetName(resolveTargetName(s.getTargetType(), s.getTargetId()));
        return dto;
    }

    @Transactional
    public SuggestionDTO createSuggestion(TargetType targetType, Long targetId,
                                          String text, Priority priority, Long createdBy) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Suggestion text is required.");
        if (text.length() > 1000)
            throw new IllegalArgumentException("Suggestion text cannot exceed 1000 characters.");

        if (targetType == TargetType.EVENT) {
            eventRepository.findById(targetId)
                    .orElseThrow(() -> new EventNotFoundException(targetId));
        } else {
            menuRepository.findById(targetId)
                    .orElseThrow(() -> new MenuNotFoundException(targetId));
        }

        Suggestion suggestion = new Suggestion();
        suggestion.setTargetType(targetType);
        suggestion.setTargetId(targetId);
        suggestion.setSuggestionText(text);
        suggestion.setPriority(priority);
        suggestion.setStatus(Status.PENDING);
        suggestion.setCreatedBy(createdBy);
        suggestion.setCreatedDate(LocalDateTime.now());
        suggestion.setRead(false);

        return toDTO(suggestionRepository.save(suggestion));
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> getAllSuggestionsForAdmin() {
        return suggestionRepository.findAllOrderedByPriority()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SuggestionDTO> getSuggestionsForCreator(Long userId) {
        List<Long> eventIds = eventRepository.findAllActive().stream()
                .filter(e -> userId.equals(e.getCreatedBy()))
                .map(e -> e.getId())
                .collect(Collectors.toList());

        List<Long> menuIds = menuRepository.findAll().stream()
                .filter(m -> userId.equals(m.getCreatedBy()))
                .map(m -> m.getId())
                .collect(Collectors.toList());

        return suggestionRepository.findByCreatedByTargets(eventIds, menuIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnreadForCreator(Long userId) {
        List<Long> eventIds = eventRepository.findAllActive().stream()
                .filter(e -> userId.equals(e.getCreatedBy()))
                .map(e -> e.getId())
                .collect(Collectors.toList());

        List<Long> menuIds = menuRepository.findAll().stream()
                .filter(m -> userId.equals(m.getCreatedBy()))
                .map(m -> m.getId())
                .collect(Collectors.toList());

        if (eventIds.isEmpty() && menuIds.isEmpty()) return 0;

        List<Long> safeEventIds = eventIds.isEmpty() ? List.of(-1L) : eventIds;
        List<Long> safeMenuIds  = menuIds.isEmpty()  ? List.of(-1L) : menuIds;

        return getSuggestionJpaRepository(safeEventIds, safeMenuIds);
    }

    private long getSuggestionJpaRepository(List<Long> safeEventIds, List<Long> safeMenuIds) {
        return suggestionRepository.findByCreatedByTargets(safeEventIds, safeMenuIds)
                .stream().filter(s -> !s.isRead()).count();
    }

    @Transactional
    public void markAllReadForCreator(Long userId) {
        List<Long> eventIds = eventRepository.findAllActive().stream()
                .filter(e -> userId.equals(e.getCreatedBy()))
                .map(e -> e.getId())
                .collect(Collectors.toList());

        List<Long> menuIds = menuRepository.findAll().stream()
                .filter(m -> userId.equals(m.getCreatedBy()))
                .map(m -> m.getId())
                .collect(Collectors.toList());

        if (eventIds.isEmpty() && menuIds.isEmpty()) return;

        suggestionRepository.findByCreatedByTargets(eventIds, menuIds)
                .stream().filter(s -> !s.isRead())
                .forEach(s -> {
                    s.setRead(true);
                    suggestionRepository.save(s);
                });
    }

    @Transactional
    public void fulfill(Long suggestionId, Long adminUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found."));
        suggestion.setStatus(Status.FULFILLED);
        suggestion.setFulfilledBy(adminUserId);
        suggestion.setFulfilledDate(LocalDateTime.now());
        suggestion.setRead(true);
        suggestionRepository.save(suggestion);
    }

    @Transactional
    public void discard(Long suggestionId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found."));
        suggestion.setStatus(Status.DISCARDED);
        suggestion.setRead(true);
        suggestionRepository.save(suggestion);
    }

    @Transactional
    public void resolveByCreator(Long suggestionId, Long currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found."));
        suggestion.setStatus(Status.FULFILLED);
        suggestion.setRead(true);
        suggestionRepository.save(suggestion);
    }
}