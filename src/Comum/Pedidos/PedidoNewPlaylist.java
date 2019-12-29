package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;

public class PedidoNewPlaylist extends Pedido {

    String nome;

    public PedidoNewPlaylist(Utilizador cliente, String nome){
        super(cliente, TipoPedido.PedidoNewPlaylist);
        this.nome = nome;
    }

    public PedidoNewPlaylist(String nome) {
        super(TipoPedido.PedidoNewPlaylist);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
