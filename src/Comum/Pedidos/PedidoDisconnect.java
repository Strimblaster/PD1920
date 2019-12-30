package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;

public class PedidoDisconnect extends Pedido {

    Pedido pedido;

    public PedidoDisconnect(Utilizador cliente, Pedido pedido) {
        super(cliente, TipoPedido.PedidoDisconnect);
        this.pedido = pedido;
    }

    public PedidoDisconnect() {
        super(TipoPedido.PedidoDisconnect);
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
