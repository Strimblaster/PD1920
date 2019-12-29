package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Song;
import Comum.Utilizador;

public class PedidoEditSong extends Pedido {

    Song musica;

    public PedidoEditSong(Utilizador cliente, Song musica) {
        super(cliente, TipoPedido.PedidoEditSong);
        this.musica = musica;
    }

    public PedidoEditSong() {
        super(TipoPedido.PedidoEditSong);
    }

    public Song getMusica() {
        return musica;
    }

    public void setMusica(Song musica) {
        this.musica = musica;
    }
}
