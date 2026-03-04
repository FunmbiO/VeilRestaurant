package nbcc.auth.client;

import nbcc.auth.domain.*;
import nbcc.auth.result.AuthResult;

public interface UserClient {

    AuthResult<UserResponse> register(UserRegistration userRegistration);

    AuthResult<UserResponse> isAuthorized(LoginRequest loginRequest);

    AuthResult<UserResponse> getUser(String username);

    AuthResult<UserResponse> getProfile();
}
