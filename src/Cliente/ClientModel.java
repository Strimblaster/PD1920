package Cliente;

import Cliente.Interfaces.Observable;
import Comum.*;
import Comum.Exceptions.*;

import java.io.*;
import java.util.ArrayList;


class ClientModel implements Constants {

    private ServerInfo server;
    private Utilizador utilizador;
    private Observable comunicacao;

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



    public boolean login(String username, String password) throws InvalidPasswordException, InvalidUsernameException {
        if(comunicacao.login(username, password)) {
            utilizador = new Utilizador(username, password);
            return true;
        }
        return false;
    }

    public void signUp(String username, String password) throws InvalidPasswordException, InvalidUsernameException, InvalidServerException {
        comunicacao.signUp(username, password);
    }

    public void uploadFile(Song musica) throws InvalidSongDescriptionException {
        comunicacao.uploadFile(utilizador, musica);
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

    public boolean newPlaylist(String nome) throws InvalidPlaylistNameException {
        return comunicacao.newPlaylist(utilizador,nome);
    }

    public ArrayList<Playlist> getPlaylists(){
        return comunicacao.getPlaylists(utilizador);
    }

    public boolean addSong(Playlist playlist, Song song) throws InvalidPlaylistNameException, InvalidSongDescriptionException {
        return comunicacao.addSong(utilizador, playlist, song);
    }

    public boolean editFile(Song song) {
        return comunicacao.editFile(utilizador, song);
    }

    public boolean editPlaylist(Playlist playlist) {
        return comunicacao.editPlaylist(utilizador, playlist);
    }

}
