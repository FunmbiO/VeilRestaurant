package nbcc.resto.persistence;

import nbcc.resto.entity.Suggestion;
import nbcc.resto.entity.Suggestion.TargetType;
import nbcc.resto.repository.SuggestionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SuggestionRepositoryImpl implements SuggestionRepository {

    private final SuggestionJpaRepository jpa;

    public SuggestionRepositoryImpl(SuggestionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Suggestion save(Suggestion suggestion) {
        return jpa.save(SuggestionJpaEntity.fromDomain(suggestion)).toDomain();
    }

    @Override
    public Optional<Suggestion> findById(Long id) {
        return jpa.findById(id).map(SuggestionJpaEntity::toDomain);
    }

    @Override
    public List<Suggestion> findAllOrderedByPriority() {
        return jpa.findAllByOrderByPriorityAscCreatedDateDesc()
                .stream().map(SuggestionJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Suggestion> findByTargetTypeAndTargetId(TargetType targetType, Long targetId) {
        return jpa.findByTargetTypeAndTargetId(targetType, targetId)
                .stream().map(SuggestionJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Suggestion> findByCreatedByTargets(List<Long> eventIds, List<Long> menuIds) {
        if (eventIds.isEmpty() && menuIds.isEmpty()) return List.of();
        List<Long> safeEventIds = eventIds.isEmpty() ? List.of(-1L) : eventIds;
        List<Long> safeMenuIds  = menuIds.isEmpty()  ? List.of(-1L) : menuIds;
        return jpa.findByTargets(safeEventIds, safeMenuIds)
                .stream().map(SuggestionJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadForUser(Long userId) { return 0; }

    @Override
    public void markAllReadForUser(Long userId) {}
}