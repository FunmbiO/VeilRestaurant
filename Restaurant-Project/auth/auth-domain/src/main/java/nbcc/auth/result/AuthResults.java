package nbcc.auth.result;

import nbcc.common.validation.ValidationError;

import java.util.Collection;

class AuthResults<T> implements AuthResult<T> {

    private final T value;
    private final boolean successful;
    private final boolean authenticated;
    private final boolean error;
    private final Collection<ValidationError> validationErrors;


    protected AuthResults(T value, boolean successful, boolean authenticated, boolean error,
                          Collection<ValidationError> validationErrors) {
        this.value = value;
        this.successful = successful;
        this.authenticated = authenticated;
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
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public boolean isSuccessful() {
        return this.successful;
    }

    @Override
    public boolean isError() {
        return error;
    }


}