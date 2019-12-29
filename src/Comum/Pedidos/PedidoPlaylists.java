package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Playlist;
import Comum.Utilizador;

import java.util.ArrayList;

public class PedidoPlaylists extends Pedido {

    ArrayList<Playlist> playlists;

    public PedidoPlaylists(Utilizador cliente){
        super(cliente, TipoPedido.PedidoPlaylists);
    }

    public PedidoPlaylists() {
        super(TipoPedido.PedidoPlaylists);
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
}
