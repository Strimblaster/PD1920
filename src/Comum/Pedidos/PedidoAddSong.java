package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;
import com.google.gson.annotations.Expose;

public class PedidoAddSong extends Pedido {

    @Expose
    Song song;
    Playlist playlist;

    public PedidoAddSong(Utilizador cliente, Song song, Playlist playlist) {
        super(cliente, TipoPedido.PedidoAddSong);
        this.song = song;
        this.playlist = playlist;
    }

    public PedidoAddSong(Song song) {
        super(TipoPedido.PedidoAddSong);
        this.song = song;
    }

    public Song getSong() {
        return song;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
