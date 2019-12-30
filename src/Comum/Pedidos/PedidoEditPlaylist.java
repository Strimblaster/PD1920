package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Playlist;
import Comum.Utilizador;

public class PedidoEditPlaylist extends Pedido{
    Playlist playlist;

    public PedidoEditPlaylist(Utilizador cliente, Playlist playlist) {
        super(cliente, TipoPedido.PedidoEditPlaylist);
        this.playlist = playlist;
    }

    public PedidoEditPlaylist() {
        super(TipoPedido.PedidoEditPlaylist);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}

