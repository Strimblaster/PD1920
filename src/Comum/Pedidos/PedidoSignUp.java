package Comum.Pedidos;

import Comum.Utilizador;

public class PedidoSignUp extends Pedido {

    public PedidoSignUp(Utilizador cliente) {
        super(cliente, TipoPedido.PedidoSignUp);
    }

    public PedidoSignUp() {
        super(TipoPedido.PedidoSignUp);
    }
}
