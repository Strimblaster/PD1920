package Servidor.Interfaces;

import Comum.*;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Observable extends IComunicacao {

    void setID(int id);
    void saveSongFile_Full(Utilizador utilizador, Song musica, byte[] file);
    void saveSongFile_Partial(Song musica, byte[] file);
    void insertUser(Utilizador utilizador) throws SQLException;
    void insertPlaylist(Utilizador utilizador, String nomePlaylist) throws SQLException;
    void insertSong(Utilizador utilizador, Playlist playlist, Song song);
    String checkSong(Song musica);
    void sync(ServerInfo serverInfo);


}
