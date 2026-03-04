package nbcc.common.validation;

public class ValidationError {

    private String field;
    private String message;
    private Object value;

    public ValidationError() {
        this(null, null, null);
    }

    public ValidationError(String message) {
        this(message, null);
    }

    public ValidationError(String message, String field) {
        this(message, field, null);
    }

    public ValidationError(String message, String field, Object value) {
        this.field = field;
        this.message = message;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public ValidationError setField(String field) {
        this.field = field;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ValidationError setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public ValidationError setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                ", value=" + value +
                '}';
    }
}
