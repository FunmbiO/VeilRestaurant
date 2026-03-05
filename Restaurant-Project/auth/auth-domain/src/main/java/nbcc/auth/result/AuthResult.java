package nbcc.auth.result;

import nbcc.common.result.ValidatedResult;
import nbcc.common.validation.ValidationError;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthResult<T> extends ValidatedResult<T> {

    boolean isAuthenticated();

    static <T> AuthResult<T> success() {
        return response(null, true, false, false);
    }

    static <T> AuthResult<T> success(T data) {
        return response(data, true, false, false);
    }

     static <T> AuthResult<T> success(T data, boolean authenticated) {
        return response(data, true, authenticated, false);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> AuthResult<T> success(Optional<T> optionalData) {
        return optionalData.map(AuthResult::success).orElseGet(AuthResult::success);
    }

    static <T> AuthResult<T> invalid(ValidationError... validationError) {
        return invalid(List.of(validationError));
    }

    static <T> AuthResult<T> invalid(Collection<ValidationError> validationErrors) {
        return invalid(null, validationErrors);
    }

    static <T> AuthResult<T> invalid(T data, Collection<ValidationError> validationErrors) {
        return response(data, false, false, false, validationErrors);
    }

    static <T> AuthResult<T> error() {
        return error(null);
    }

    static <T> AuthResult<T> error(T data) {
        return response(data, false, false, true);
    }

    static <T> AuthResult<T> error(Exception e) {
        return response(null, false, false, true);
    }

    private static <T> AuthResult<T> response(T data, boolean successful, boolean authenticated, boolean error) {
        return response(data, successful, authenticated, error, List.of());
    }

    private static <T> AuthResult<T> response(T data, boolean successful, boolean authenticated, boolean error, Collection<ValidationError> validationErrors) {
        return new AuthResults<>(data, successful, authenticated, error, validationErrors);
    }
}
