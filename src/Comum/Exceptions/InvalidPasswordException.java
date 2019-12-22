package Comum.Exceptions;

public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {
        super("Password Invalida");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
