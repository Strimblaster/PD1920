package Servidor.Interfaces;

import Comum.IComunicacao;
import Comum.Song;

public interface IServer extends IComunicacao {

    void setID(int id);
    void addNewSong(Song musica, byte[] file);
}
