package Comum.Exceptions;

public class InvalidPlaylistNameException extends Exception {

    public InvalidPlaylistNameException() {
        super("Nome da playlist já existe");
    }

    public InvalidPlaylistNameException(String message) {
        super(message);
    }
}
