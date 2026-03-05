package nbcc.common.validation;

import java.util.Collection;

public interface HasValidationErrors {

    Collection<ValidationError> getValidationErrors();

    default boolean isInvalid(){
        return getValidationErrors() != null && !getValidationErrors().isEmpty();
    }
}
