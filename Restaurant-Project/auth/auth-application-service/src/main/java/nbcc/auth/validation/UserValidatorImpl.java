package nbcc.auth.validation;

import nbcc.auth.domain.UserRegistration;
import nbcc.auth.repository.UserRepository;
import nbcc.common.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;

    public UserValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<ValidationError> validate(UserRegistration userRegistration) {

        var errors = new ArrayList<ValidationError>();

        if(userRegistration.getUsername() == null || userRegistration.getUsername().isBlank()){
            errors.add(new ValidationError("Username is required", "username"));
        } else {
            if(userRepository.userExists(userRegistration.getUsername())){
                errors.add(new ValidationError("Username already exists", "username", userRegistration.getUsername()));
            }
        }

        if(userRegistration.getPassword() == null || userRegistration.getPassword().isBlank()){
            errors.add(new ValidationError("Password is required", "password", userRegistration.getPassword()));
        }

        if(userRegistration.getEmail() == null || userRegistration.getEmail().isBlank()){
            errors.add(new ValidationError("Email is required", "email"));
        } else {
            if(userRepository.emailExists(userRegistration.getEmail())){
                errors.add(new ValidationError("Email already in use", "email", userRegistration.getEmail()));
            }
        }

        return errors;
    }
}
