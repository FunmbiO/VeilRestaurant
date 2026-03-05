package nbcc.auth.service;

import nbcc.auth.domain.LoginRequest;
import nbcc.auth.domain.UserRegistration;
import nbcc.auth.domain.UserResponse;
import nbcc.auth.repository.UserRepository;
import nbcc.auth.result.AuthResult;
import nbcc.auth.validation.UserValidator;
import nbcc.auth.validation.LoginRequestValidator;
import nbcc.common.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final LoginRequestValidator loginRequestValidationService;
    private final UserRepository userRepository;
    private final UserValidator userValidationService;

    public UserServiceImpl(LoginRequestValidator loginRequestValidationService, UserRepository userRepository, UserValidator userValidationService) {
        this.loginRequestValidationService = loginRequestValidationService;
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    @Override
    public AuthResult<UserResponse> register(UserRegistration userRegistration) {
        try {
            var validationErrors = userValidationService.validate(userRegistration);

            if(validationErrors.isEmpty()){
                return AuthResult.success(userRepository.create(userRegistration));
            }

            // don't call the create method if there are validation validationErrors
            logger.debug("Validation validationErrors for user registration {}: {}", userRegistration, validationErrors);
            return AuthResult.invalid(new UserResponse(userRegistration), validationErrors);

        } catch (Exception e) {
            logger.error("Error registering user: {} , {}", userRegistration , e.getMessage());
            return AuthResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> isAuthorized(LoginRequest loginRequest) {

        var errors = loginRequestValidationService.validate(loginRequest);

        if(!errors.isEmpty()){
            return AuthResult.invalid(errors);
        }

        try {
            var isValid = userRepository.isValid(loginRequest.getUsername(), loginRequest.getPassword());

            if(isValid){
                var optionalUser = userRepository.get(loginRequest.getUsername());
                return optionalUser
                        .map(data -> AuthResult.success(data, true))
                        .orElseGet(AuthResult::invalid);
            }

            return AuthResult.invalid(new ValidationError("Invalid username or password"));

        } catch (Exception e) {
            logger.error("Error checking user authorization: {} , {}", loginRequest.getUsername(), e.getMessage());
            return AuthResult.error(e);
        }
    }

    @Override
    public AuthResult<UserResponse> get(String username) {
        try {
            return AuthResult.success(userRepository.get(username));
        } catch (Exception e) {
            logger.error("Error retrieving user with username: {}", username, e);
            return AuthResult.error(e);
        }
    }
}
