package nbcc.resto.persistence;

import nbcc.resto.entity.Suggestion.Priority;
import nbcc.resto.entity.Suggestion.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestionJpaRepository extends JpaRepository<SuggestionJpaEntity, Long> {

    List<SuggestionJpaEntity> findAllByOrderByPriorityAscCreatedDateDesc();

    List<SuggestionJpaEntity> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);

    @Query("""
        SELECT s FROM SuggestionJpaEntity s
        WHERE (s.targetType = 'EVENT' AND s.targetId IN :eventIds)
           OR (s.targetType = 'MENU'  AND s.targetId IN :menuIds)
        ORDER BY s.priority ASC, s.createdDate DESC
    """)
    List<SuggestionJpaEntity> findByTargets(
            @Param("eventIds") List<Long> eventIds,
            @Param("menuIds")  List<Long> menuIds);

    @Query("""
        SELECT COUNT(s) FROM SuggestionJpaEntity s
        WHERE s.isRead = false
          AND ((s.targetType = 'EVENT' AND s.targetId IN :eventIds)
            OR (s.targetType = 'MENU'  AND s.targetId IN :menuIds))
    """)
    long countUnread(
            @Param("eventIds") List<Long> eventIds,
            @Param("menuIds")  List<Long> menuIds);

    @Modifying
    @Query("""
        UPDATE SuggestionJpaEntity s SET s.isRead = true
        WHERE s.isRead = false
          AND ((s.targetType = 'EVENT' AND s.targetId IN :eventIds)
            OR (s.targetType = 'MENU'  AND s.targetId IN :menuIds))
    """)
    void markAllRead(
            @Param("eventIds") List<Long> eventIds,
            @Param("menuIds")  List<Long> menuIds);
}
