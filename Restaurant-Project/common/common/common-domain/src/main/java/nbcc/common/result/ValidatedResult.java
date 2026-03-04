package nbcc.common.result;
import nbcc.common.validation.HasValidationErrors;
import nbcc.common.validation.ValidationError;

import java.util.Collection;

public interface ValidatedResult<T> extends Result<T>, HasValidationErrors {

    Collection<ValidationError> getValidationErrors();

    default boolean isInvalid() {

        if(getValidationErrors() == null){
            return false;
        }

        return !getValidationErrors().isEmpty();
    }

    default boolean isSuccessful() {
        return !isInvalid() && !isError();
    }
}
