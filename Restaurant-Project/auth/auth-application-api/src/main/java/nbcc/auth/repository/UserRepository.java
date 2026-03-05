package nbcc.auth.repository;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;

import java.util.Optional;

public interface UserRepository {

    UserResponse create(UserRegistration userLogin);

    Optional<UserResponse> get(long id);

    Optional<UserResponse> get(String username);

    boolean isValid(String username, String password);

    boolean userExists(String username);

    boolean emailExists(String email);
}
