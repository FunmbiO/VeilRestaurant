package nbcc.common.validation;

import nbcc.common.result.ValidatedResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ModelErrorConverter {

    public static <T> BindingResult addErrorsToBindingResults(BindingResult bindingResult, ValidatedResult<T> result) {
        return addErrorsToBindingResults(bindingResult, result, result.getValue().getClass());
    }

    public static <T> BindingResult addErrorsToBindingResults(BindingResult bindingResult, ValidatedResult<T> result, String objectName) {
        return addErrorsToBindingResults(bindingResult, result.getValidationErrors(), objectName);
    }

    public static <T> BindingResult addErrorsToBindingResults(BindingResult bindingResult, ValidatedResult<T> result, Class<?> clazz) {
        return addErrorsToBindingResults(bindingResult, result.getValidationErrors(), clazz.getSimpleName());
    }

    public static BindingResult addErrorsToBindingResults(BindingResult bindingResult, Iterable<ValidationError> errors, String objectName) {
        for (ValidationError error : errors) {
            if (error.getField() == null || error.getField().isEmpty()) {
                bindingResult.addError(new ObjectError(objectName, error.getMessage()));
            } else {
                //bindingResult.addError(new FieldError(objectName, error.getField(), error.getMessage()));
                bindingResult.addError(new FieldError(objectName, error.getField(), error.getValue(), false, null, null, error.getMessage()));
            }
        }

        return bindingResult;
    }
}