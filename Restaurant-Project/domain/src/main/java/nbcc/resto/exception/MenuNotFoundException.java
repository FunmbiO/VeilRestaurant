package nbcc.resto.exception;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(Long id) {
        super("Menu not found with id: " + id);
    }
}
