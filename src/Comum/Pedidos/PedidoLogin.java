package Comum.Pedidos;

import Comum.Utilizador;

public class PedidoLogin extends Pedido {

    public PedidoLogin(Utilizador cliente) {
        super(cliente, TipoPedido.PedidoLogin);
    }

    public PedidoLogin() {
        super(TipoPedido.PedidoLogin);
    }
}
