package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Song;
import Comum.Utilizador;

public class PedidoUploadFile extends Pedido {

    Song musica;

    public PedidoUploadFile(Utilizador cliente, Song musica) {
        super(cliente, TipoPedido.PedidoUploadFile);
        this.musica = musica;
    }

    public PedidoUploadFile() {
        super(TipoPedido.PedidoUploadFile);
    }

    public Song getMusica() {
        return musica;
    }

    public void setMusica(Song musica) {
        this.musica = musica;
    }
}
