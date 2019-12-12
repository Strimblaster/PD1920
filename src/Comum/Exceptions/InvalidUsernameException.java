package Comum.Exceptions;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException() {
        super("Username invalido");
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
