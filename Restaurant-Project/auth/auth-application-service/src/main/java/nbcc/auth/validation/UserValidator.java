package nbcc.auth.validation;
import nbcc.auth.domain.UserRegistration;
import nbcc.common.validation.ValidationError;

import java.util.Collection;

public interface UserValidator {
    Collection<ValidationError> validate(UserRegistration userRegistration);
}
