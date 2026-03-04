package nbcc.common.result;

import nbcc.common.validation.ValidationError;
import nbcc.common.validation.ValidationResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ValidationResults<T> implements ValidatedResult<T> {

    private final T value;
    private final boolean successful;
    private final boolean error;
    private final Collection<ValidationError> validationErrors;


    protected ValidationResults(T value, boolean successful, boolean error,
                                Collection<ValidationError> validationErrors) {
        this.value = value;
        this.successful = successful;
        this.error = error;
        this.validationErrors = validationErrors;
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean isSuccessful() {
        return this.successful;
    }

    @Override
    public boolean isError() {
        return error;
    }

    public static<T> ValidationResults<T> success() {
        return response(null, true, false);
    }

    public static<T> ValidationResults<T> success(T data) {
        return response(data, true, false);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static<T> ValidationResults<T> success(Optional<T> optionalData) {
        return optionalData.map(ValidationResults::success).orElseGet(ValidationResults::success);
    }

    public static<T> ValidationResults<T> invalid(T data, String message) {
        return invalid(data,  new ValidationError(message));
    }

    public static<T> ValidationResults<T> invalid(String message, String field) {
        return invalid(null,  new ValidationError(message, field));
    }

    public static<T> ValidationResults<T> invalid(T data, String message, String field) {
        return invalid(data,  new ValidationError(message));
    }

    public static<T> ValidationResults<T> invalid(T data, ValidationError... validationError) {
        return invalid(data, List.of(validationError));
    }

    public static<T> ValidationResults<T> invalid(T data, Collection<ValidationError> validationErrors) {
        if(data instanceof ValidationResponse validationResponse){
            validationResponse.setValidationErrors(validationErrors);
        }

        return response(data, false, false, validationErrors);
    }

    public static<T> ValidationResults<T> error() {
        return error(null);
    }

    public static<T> ValidationResults<T> error(T data) {
        return response(data, false, true);
    }

    public static<T> ValidationResults<T> error(Exception e) {
        return response(null, false, true);
    }

    private static<T> ValidationResults<T> response(T data, boolean successful, boolean error) {
        return response(data, successful, error, List.of());
    }

    private static<T> ValidationResults<T> response(T data, boolean successful, boolean error, Collection<ValidationError> validationErrors) {
        return new ValidationResults<>(data, successful, error, validationErrors);
    }
}