package nbcc.resto.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuJpaRepository extends JpaRepository<MenuJpaEntity, Long> {
    boolean existsByName(String name);
    Optional<MenuJpaEntity> findByName(String name);
    List<MenuJpaEntity> findAllByOrderByCreatedDateTimeDesc();
    boolean existsByNameAndIdNot(String name, Long id);

    // US6 - Search
    @Query("SELECT m FROM MenuJpaEntity m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY m.createdDateTime DESC")
    List<MenuJpaEntity> searchByName(@Param("name") String name);
}