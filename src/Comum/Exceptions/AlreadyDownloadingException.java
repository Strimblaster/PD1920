package Comum.Exceptions;

public class AlreadyDownloadingException extends Exception {
    public AlreadyDownloadingException(String nome) {
        super("Já está a ser feito o download da musica " + nome);
    }
}
