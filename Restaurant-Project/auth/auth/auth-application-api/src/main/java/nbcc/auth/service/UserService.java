package nbcc.auth.service;

import nbcc.auth.domain.LoginRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.result.AuthResult;

public interface UserService {

    AuthResult<UserResponse> register(UserRegistration userRegistration);

    AuthResult<UserResponse> isAuthorized(LoginRequest loginRequest);

    AuthResult<UserResponse> get(String username);

}
