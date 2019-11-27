package Cliente;

import Comum.IComunicacao;
import java.net.Socket;

public class Comunicacao implements IComunicacao {
    Socket s;

    public Comunicacao(Socket s) {
        this.s = s;
    }
}
