package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.Constants;
import Comum.Exceptions.InvalidServerException;
import Comum.ServerInfo;
import Comum.Utilizador;
import Cliente.Interfaces.IEvent;

import java.io.*;

//O IEvent talvez acabe por não ficar aqui, depende depois de como for fazer a grafica
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
}
