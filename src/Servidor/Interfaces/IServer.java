package Servidor.Interfaces;

import Comum.IComunicacao;
import Comum.Song;
import Comum.Utilizador;

import java.sql.SQLException;

public interface IServer extends IComunicacao {

    void setID(int id);
    void addNewSong(Song musica, byte[] file);
    void insertUser(Utilizador utilizador) throws SQLException;
}
