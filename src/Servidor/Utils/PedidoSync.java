package Servidor.Utils;

import Comum.Pedidos.Pedido;

public class PedidoSync {

    Pedido pedido;
    String file;

    public PedidoSync(Pedido pedido, String file) {
        this.pedido = pedido;
        this.file = file;
    }

    public PedidoSync() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
