package Comum.Exceptions;

public class InvalidSongDescriptionException extends Exception {

    public InvalidSongDescriptionException() {
        super("Nome da musica já existente");
    }

    public InvalidSongDescriptionException(String message) {
        super(message);
    }
}
