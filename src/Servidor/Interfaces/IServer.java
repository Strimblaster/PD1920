package Servidor.Interfaces;

import Comum.IComunicacao;
import Comum.Song;
import Comum.Utilizador;

import java.sql.SQLException;

public interface IServer extends IComunicacao {

    void setID(int id);
    void saveSongFile_Full(Utilizador utilizador, Song musica, byte[] file);
    void saveSongFile_Partial(Song musica, byte[] file);
    void insertUser(Utilizador utilizador) throws SQLException;
    void insertPlaylist(Utilizador utilizador, String nomePlaylist) throws SQLException;

}
