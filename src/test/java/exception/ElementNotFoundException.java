package exception;

public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}