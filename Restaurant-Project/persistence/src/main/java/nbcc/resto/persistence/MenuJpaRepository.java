package nbcc.resto.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuJpaRepository extends JpaRepository<MenuJpaEntity, Long> {
    boolean existsByName(String name);
    Optional<MenuJpaEntity> findByName(String name);
    List<MenuJpaEntity> findAllByOrderByCreatedDateTimeDesc();
}