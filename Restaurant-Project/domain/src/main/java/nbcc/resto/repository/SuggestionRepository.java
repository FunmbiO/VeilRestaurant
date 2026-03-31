package nbcc.resto.repository;

import nbcc.resto.entity.Suggestion;
import nbcc.resto.entity.Suggestion.TargetType;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository {
    Suggestion save(Suggestion suggestion);
    Optional<Suggestion> findById(Long id);
    List<Suggestion> findAllOrderedByPriority();
    List<Suggestion> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    List<Suggestion> findByCreatedByTargets(List<Long> eventIds, List<Long> menuIds);
    long countUnreadForUser(Long userId);
    void markAllReadForUser(Long userId);
}