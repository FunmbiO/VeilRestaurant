package nbcc.auth.service;

import nbcc.auth.client.UserClient;
import nbcc.auth.domain.LoginRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.result.AuthClientResult;
import nbcc.auth.result.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserClientServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserClientServiceImpl.class);

    private final UserClient userClient;

    public UserClientServiceImpl(UserClient userClient) {
        this.userClient = userClient;
    }


    @Override
    public AuthResult<UserResponse> register(UserRegistration userRegistration) {
        try {
            return userClient.register(userRegistration);
        } catch (Exception e) {
            logger.error("Error registering user: {} , {}", userRegistration , e.getMessage());
            return AuthClientResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> isAuthorized(LoginRequest loginRequest) {
        try {
            return userClient.isAuthorized(loginRequest);
        } catch (Exception e) {
            logger.error("Error validating user: {}", loginRequest.getUsername(), e);
            return AuthClientResult.error(e);
        }
    }


    @Override
    public AuthResult<UserResponse> get(String username) {
        try {
            return userClient.getUser(username);
        } catch (Exception e) {
            logger.error("Error retrieving user with username: {}", username, e);
            return AuthClientResult.error(e);
        }
    }
}
