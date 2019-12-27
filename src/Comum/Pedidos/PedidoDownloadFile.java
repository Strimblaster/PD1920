package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Song;
import Comum.Utilizador;

public class PedidoDownloadFile extends Pedido {

    Song musica;

    public PedidoDownloadFile(Utilizador cliente, Song musica) {
        super(cliente, TipoPedido.PedidoDownloadFile);
        this.musica = musica;
    }

    public PedidoDownloadFile() {
        super(TipoPedido.PedidoUploadFile);
    }

    public Song getMusica() {
        return musica;
    }

    public void setMusica(Song musica) {
        this.musica = musica;
    }
}
