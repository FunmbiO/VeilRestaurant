package nbcc.common.validation;

import java.util.Collection;

public interface ValidationResponse extends HasValidationErrors {

    Collection<ValidationError> getValidationErrors();

    ValidationResponse setValidationErrors(Collection<ValidationError> validationErrors);
}
