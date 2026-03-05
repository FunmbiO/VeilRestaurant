package nbcc.common.result;

public interface Result<T> {

    T getValue();

    default boolean isEmpty() {
        return !hasValue();
    }

    default boolean hasValue(){
        return getValue() != null;
    }

    default boolean isSuccessful() {
        return !hasValue();
    }

    default boolean isError() {
        return false;
    }
}
