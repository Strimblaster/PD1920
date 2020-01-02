package Servidor.Interfaces;

import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;

import java.io.IOException;

public interface IEvent {


    void newUser(String username, String password);
    void newPlaylist(Utilizador utilizador, String nomePlaylist);
    void newSong(Utilizador utilizador, Song song);
    void newSongFile(Utilizador utilizador, Song song, byte[] file);
    void newSongPlaylist(Utilizador utilizador, Playlist playlist, Song song);

    void serverReady();
    void needID() throws IOException;
    void serverExit();

}
