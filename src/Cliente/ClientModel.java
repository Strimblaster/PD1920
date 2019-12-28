package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.*;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Cliente.Interfaces.IEvent;

import java.io.*;
import java.util.ArrayList;


class ClientModel implements Constants {

    private ServerInfo server;
    private Utilizador utilizador;
    private IComunicacaoCliente comunicacao;

    public ClientModel(ClientController clientController) throws IOException, InvalidServerException {
        comunicacao = new Comunicacao(clientController);
        this.server = comunicacao.getServerInfo();

    }

    public ServerInfo getServer() {
        return server;
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

    public void downloadFile(Song musica) {
        comunicacao.downloadFile(utilizador, musica);
    }

    public ArrayList<Song> getMyMusics() {
        return comunicacao.getMusicas(utilizador);
    }

    public FilteredResult search(boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        return comunicacao.search(utilizador, songs, playlists, nome, album, genero, ano, duracao);
    }

    public void setFileDir(File musicDirectory) {
        comunicacao.setMusicDir(musicDirectory);
    }
}
