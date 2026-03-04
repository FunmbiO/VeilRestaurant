package nbcc.resto.exception;

public class DuplicateEventNameException extends RuntimeException {
    public DuplicateEventNameException(String name) {
        super("An event already exists with the name: " + name);
    }
}
