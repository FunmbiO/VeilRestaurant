package nbcc.auth.repository;

import nbcc.auth.entity.UserLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginJPARepository extends JpaRepository<UserLoginEntity, Long> {
    Optional<UserLoginEntity> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
