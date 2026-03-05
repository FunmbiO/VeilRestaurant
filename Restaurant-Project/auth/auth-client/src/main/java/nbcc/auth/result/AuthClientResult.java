package nbcc.auth.result;

import nbcc.common.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;


public class AuthClientResult<T> implements AuthResult<T> {

    private static final Logger logger = LoggerFactory.getLogger(AuthClientResult.class);

    private int statusCode;
    private T value;
    private boolean successful;
    private boolean authenticated;
    private boolean error;
    private Collection<ValidationError> validationErrors;

    public AuthClientResult() {
    }

    protected AuthClientResult(int statusCode, T value, boolean successful, boolean authenticated, boolean error,
                               Collection<ValidationError> validationErrors) {
        this();
        this.statusCode = statusCode;
        this.value = value;
        this.successful = successful;
        this.authenticated = authenticated;
        this.error = error;
        this.validationErrors = validationErrors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public AuthClientResult<T> setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public T getValue() {
        return value;
    }

    public AuthClientResult<T> setValue(T value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isSuccessful() {
        return successful;
    }

    public AuthClientResult<T> setSuccessful(boolean successful) {
        this.successful = successful;
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    public AuthClientResult<T> setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    @Override
    public boolean isError() {
        return error;
    }

    public AuthClientResult<T> setError(boolean error) {
        this.error = error;
        return this;
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public AuthClientResult<T> setValidationErrors(Collection<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }

    public static <T> AuthClientResult<T> error() {

        return response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> AuthClientResult<T> error(Exception e) {

        return response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> AuthClientResult<T> response(ResponseEntity<AuthClientResult<T>> responseEntity) {
        var result = responseEntity.getBody();
        if (result != null) {
            result.setStatusCode(responseEntity.getStatusCode().value());
        }
        return result;
    }

    public static <T> AuthClientResult<T> response(HttpClientErrorException e, Class<T> targetType) {
        return response(e, new ParameterizedTypeReference<>() {
        });
    }

    public static <T> AuthClientResult<T> response(HttpClientErrorException e, ParameterizedTypeReference<AuthClientResult<T>> bodyType) {
        try {
            if (!e.getResponseBodyAsString().isEmpty()) {

                var responseBody = e.getResponseBodyAs(bodyType);
                if (responseBody != null) {
                    return responseBody.setStatusCode(e.getStatusCode().value());
                }
            }
            return response(e.getStatusCode());

        } catch (Exception convertException) {
            logger.error("Error parsing response to body type {}", bodyType.getType().getTypeName(), convertException);
            return error(convertException);
        }
    }

    public static <T> AuthClientResult<T> response(HttpStatusCode statusCode) {

        var success = statusCode.is2xxSuccessful();
        var error = statusCode.is5xxServerError();

        return new AuthClientResult<>(statusCode.value(), null, success, false, error, List.of());
    }
}