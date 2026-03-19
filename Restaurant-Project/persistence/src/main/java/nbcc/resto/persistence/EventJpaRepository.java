package nbcc.resto.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {

    // Existing methods
    Optional<EventJpaEntity> findByName(String name);
    boolean existsByName(String name);
    List<EventJpaEntity> findByActiveTrueOrderByStartDateAsc();
    boolean existsByNameAndIdNot(String name, Long id);

    // US7 - Search Events
    // All parameters are optional — passing null skips that filter.
    @Query("SELECT e FROM EventJpaEntity e WHERE " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:startDate IS NULL OR e.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.startDate <= :endDate)")
    List<EventJpaEntity> searchEvents(
            @Param("name")      String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate")   LocalDateTime endDate
    );
}