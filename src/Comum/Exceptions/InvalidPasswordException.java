package Comum.Exceptions;

public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {
        super("Username invalido");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
