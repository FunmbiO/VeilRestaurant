package nbcc.resto.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {
    Optional<EventJpaEntity> findByName(String name);
    boolean existsByName(String name);
    List<EventJpaEntity> findByActiveTrueOrderByStartDateAsc();
    boolean existsByNameAndIdNot(String name, Long id);
}