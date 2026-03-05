package nbcc.auth.validation;

import nbcc.auth.domain.LoginRequest;
import nbcc.common.validation.ValidationError;

import java.util.Collection;

public interface LoginRequestValidator {
    Collection<ValidationError> validate(LoginRequest loginRequest);
}
