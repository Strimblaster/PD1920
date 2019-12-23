package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.Constants;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Comum.ServerInfo;
import Comum.Song;
import Comum.Utilizador;
import Cliente.Interfaces.IEvent;

import java.io.*;
import java.util.ArrayList;


class ClientModel implements Constants {

    private ServerInfo server;
    private Utilizador utilizador;
    private IComunicacaoCliente comunicacao;

    public ClientModel(ClientController clientController, File musicDir) throws IOException, InvalidServerException {
        comunicacao = new Comunicacao(clientController, musicDir);
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
        Resposta resposta  = comunicacao.login(username, password);
        utilizador = resposta.getPedido().getUtilizador();
        return resposta;
    }

    public Resposta signUp(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        return comunicacao.signUp(username, password);
    }

    public Resposta uploadFile(Song musica) throws InvalidSongDescriptionException {
        return comunicacao.uploadFile(utilizador, musica);
    }

    public ArrayList<Song> getMyMusics() {
        return comunicacao.getMusicas(utilizador);
    }
}
