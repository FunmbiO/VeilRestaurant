package nbcc.auth.validation;

import nbcc.auth.domain.LoginRequest;
import nbcc.common.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class LoginRequestValidatorImpl implements LoginRequestValidator {

    @Override
    public Collection<ValidationError> validate(LoginRequest loginRequest) {
        var errors = new ArrayList<ValidationError>();

        if(loginRequest.getUsername() == null || loginRequest.getUsername().isBlank()){
            errors.add(new ValidationError("Username is required", "username"));
        }

        return errors;
    }
}
