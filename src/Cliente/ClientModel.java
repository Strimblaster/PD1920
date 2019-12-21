package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.Constants;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Comum.ServerInfo;
import Comum.Utilizador;
import Cliente.Interfaces.IEvent;

import java.io.*;


class ClientModel implements Constants, IEvent {

    private ServerInfo server;
    private Utilizador utilizador;
    private IComunicacaoCliente comunicacao;

    public ClientModel() throws IOException, InvalidServerException {
        comunicacao = new Comunicacao();
        this.server = comunicacao.getServerInfo();

    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador;
    }



    public Resposta login(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        return comunicacao.login(username, password);
    }

    public Resposta  signUp(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        return comunicacao.signUp(username, password);
    }
}
