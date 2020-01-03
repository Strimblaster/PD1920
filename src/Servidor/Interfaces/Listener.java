package Servidor.Interfaces;

import Comum.Pedidos.Pedido;
import Comum.Playlist;
import Comum.ServerInfo;
import Comum.Song;
import Comum.Utilizador;
import Servidor.Utils.PedidoSync;

import java.io.IOException;

public interface Listener {


    void newUser(String username, String password);
    void newPlaylist(Utilizador utilizador, String nomePlaylist);
    void newSong(Utilizador utilizador, Song song);
    void newSongFile(Utilizador utilizador, Song song, byte[] file);
    void newSongPlaylist(Utilizador utilizador, Playlist playlist, Song song);
    void sendPedidoSync(PedidoSync pedidoSync, ServerInfo serverInfo);

    void serverReady() throws IOException;
    void needID() throws IOException;
    void serverExit();

}
