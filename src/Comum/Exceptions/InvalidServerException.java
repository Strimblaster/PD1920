package Comum.Exceptions;

public class InvalidServerException extends Exception {

    public InvalidServerException() {
        super("Servidor Invalido ou não existe nenhum disponivel");
    }
}
