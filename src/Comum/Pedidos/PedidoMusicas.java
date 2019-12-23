package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;

public class PedidoMusicas extends Pedido {
    public PedidoMusicas(Utilizador cliente) {
        super(cliente, TipoPedido.PedidoMusicas);
    }

    public PedidoMusicas() {
        super(TipoPedido.PedidoMusicas);
    }
}
