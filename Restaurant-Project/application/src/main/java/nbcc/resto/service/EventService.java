package nbcc.resto.service;

import nbcc.auth.entity.UserLoginEntity;
import nbcc.auth.repository.UserLoginJPARepository;
import nbcc.resto.dto.CreateEventRequest;
import nbcc.resto.dto.EventDTO;
import nbcc.resto.dto.EventSearchCriteria;
import nbcc.resto.dto.UpdateEventRequest;
import nbcc.resto.entity.Event;
import nbcc.resto.exception.DuplicateEventNameException;
import nbcc.resto.exception.EventNotFoundException;
import nbcc.resto.exception.InvalidEventException;
import nbcc.resto.exception.UnauthorizedException;
import nbcc.resto.repository.EventRepository;
import nbcc.resto.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MenuRepository menuRepository;
    private final UserLoginJPARepository userLoginJPARepository;

    public EventService(@Qualifier("eventRepositoryImpl") EventRepository eventRepository,
                        MenuRepository menuRepository,
                        UserLoginJPARepository userLoginJPARepository) {
        this.eventRepository       = eventRepository;
        this.menuRepository        = menuRepository;
        this.userLoginJPARepository = userLoginJPARepository;
    }

    private String resolveMenuName(Long menuId) {
        if (menuId == null) return null;
        return menuRepository.findById(menuId)
                .map(m -> m.getName())
                .orElse(null);
    }

    private String resolveUsername(Long userId) {
        if (userId == null) return null;
        return userLoginJPARepository.findById(userId)
                .map(UserLoginEntity::getUsername)
                .orElse("[Deleted User]");
    }

    private EventDTO toDTO(Event event) {
        EventDTO dto = EventDTO.from(event);
        dto.setMenuName(resolveMenuName(event.getMenuId()));
        dto.setCreatedByUsername(resolveUsername(event.getCreatedBy()));
        return dto;
    }

    // Authorization

    public void assertCanModify(Long eventId, Long currentUserId, boolean isAdmin) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        if (!isAdmin && !Objects.equals(event.getCreatedBy(), currentUserId)) {
            throw new UnauthorizedException("You do not have permission to modify this event.");
        }
    }

    public boolean canModify(Long eventId, Long currentUserId, boolean isAdmin) {
        if (isAdmin) return true;
        return eventRepository.findById(eventId)
                .map(e -> Objects.equals(e.getCreatedBy(), currentUserId))
                .orElse(false);
    }

    // Create

    @Transactional
    public EventDTO createEvent(CreateEventRequest request, Long createdBy) {
        if (request.getName() == null || request.getName().isBlank())
            throw new InvalidEventException("Event name is required.");
        if (request.getStartDate() == null)
            throw new InvalidEventException("Start date is required.");
        if (request.getEndDate() == null)
            throw new InvalidEventException("End date is required.");
        if (request.getPrice() == null)
            throw new InvalidEventException("Price is required.");
        if (eventRepository.existsByName(request.getName()))
            throw new DuplicateEventNameException(request.getName());
        if (request.isActive() && request.getMenuId() == null)
            throw new InvalidEventException("A menu must be selected before marking an event as active.");

        Event event = new Event(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDurationMinutes(),
                request.getPrice(),
                request.isActive()
        );
        event.setMenuId(request.getMenuId());
        event.setCreatedBy(createdBy);

        if (!event.isDateRangeValid())
            throw new InvalidEventException("End date must be on or after start date.");
        if (!event.isPositiveValues())
            throw new InvalidEventException("Price and duration must be positive values.");

        event.setCreatedDate(LocalDateTime.now());
        event.setUpdatedDate(LocalDateTime.now());
        return toDTO(eventRepository.save(event));
    }

    // Read

    @Transactional(readOnly = true)
    public List<EventDTO> getAllActiveEvents() {
        return eventRepository.findAllActive()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return toDTO(event);
    }

    // Delete / Archive

    @Transactional(readOnly = true)
    public boolean isEventInPast(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return event.getEndDate().isBefore(LocalDateTime.now());
    }

    @Transactional
    public boolean deleteOrArchiveEvent(Long id, Long currentUserId, boolean isAdmin) {
        assertCanModify(id, currentUserId, isAdmin);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        boolean isPast = event.getEndDate().isBefore(LocalDateTime.now());

        if (isPast) {
            event.setActive(false);
            event.setUpdatedDate(LocalDateTime.now());
            eventRepository.save(event);
        } else {
            eventRepository.deleteById(id);
        }

        return isPast;
    }

    // Update

    @Transactional
    public EventDTO updateEvent(Long id, UpdateEventRequest request, Long currentUserId, boolean isAdmin) {
        assertCanModify(id, currentUserId, isAdmin);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (request.getName() == null || request.getName().isBlank())
            throw new InvalidEventException("Event name is required.");
        if (request.getStartDate() == null)
            throw new InvalidEventException("Start date is required.");
        if (request.getEndDate() == null)
            throw new InvalidEventException("End date is required.");
        if (request.getPrice() == null)
            throw new InvalidEventException("Price is required.");
        if (eventRepository.existsByNameAndIdNot(request.getName(), id))
            throw new DuplicateEventNameException(request.getName());
        if (request.isActive() && request.getMenuId() == null)
            throw new InvalidEventException("A menu must be selected before marking an event as active.");

        Event temp = new Event(
                request.getName(), request.getDescription(),
                request.getStartDate(), request.getEndDate(),
                request.getDurationMinutes(), request.getPrice(), request.isActive()
        );
        if (!temp.isDateRangeValid())
            throw new InvalidEventException("End date must be on or after start date.");
        if (!temp.isPositiveValues())
            throw new InvalidEventException("Price and duration must be positive values.");

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setDurationMinutes(request.getDurationMinutes());
        event.setPrice(request.getPrice());
        event.setActive(request.isActive());
        event.setMenuId(request.getMenuId());
        event.setUpdatedDate(LocalDateTime.now());

        return toDTO(eventRepository.save(event));
    }

    // Search

    @Transactional(readOnly = true)
    public List<EventDTO> searchEvents(EventSearchCriteria criteria) {
        String name = (criteria.getName() == null || criteria.getName().isBlank())
                ? null : criteria.getName().trim();

        LocalDateTime from = null;
        LocalDateTime to   = null;

        if (criteria.getDateRangeFilter() != null) {
            switch (criteria.getDateRangeFilter()) {
                case "AFTER":  from = criteria.getStartDate(); break;
                case "BEFORE": to   = criteria.getStartDate(); break;
                case "BETWEEN":
                    from = criteria.getStartDate();
                    to   = criteria.getEndDate();
                    break;
                default: break;
            }
        }

        return eventRepository.searchEvents(name, from, to)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}