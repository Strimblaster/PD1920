package Comum;

import Comum.Exceptions.*;
import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoDownloadFile;
import Comum.Pedidos.PedidoUploadFile;
import com.mysql.fabric.xmlrpc.base.Array;

import java.io.File;
import java.util.ArrayList;

public interface IComunicacao {

    boolean login(String username, String password) throws InvalidUsernameException, InvalidPasswordException;
    boolean signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException, InvalidServerException;
    String uploadFile(Utilizador utilizador, Song musica) throws InvalidSongDescriptionException;
    byte [] downloadFile(Utilizador utilizador, Song musica);
    ArrayList<Song> getMusicas(Utilizador utilizador);
    FilteredResult search(Utilizador utilizador, boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao);

    boolean newPlaylist(Utilizador utilizador, String nome) throws InvalidPlaylistNameException;
    ArrayList<Playlist> getPlaylists(Utilizador utilizador);
    boolean addSong(Utilizador utilizador, Playlist playlist, Song song) throws InvalidPlaylistNameException, InvalidSongDescriptionException;

    boolean editFile(Utilizador utilizador, Song song);
    boolean editPlaylist(Utilizador utilizador, Playlist playlist);

    String uploadFileDisconnected(Utilizador utilizador, PedidoUploadFile pedido, File cliMusicDir, String filename);
    byte [] downloadFileDisconnected(Utilizador utilizador, PedidoDownloadFile pedido, File cliMusicDir);

}
