package Comum.Pedidos;

import java.net.InetAddress;

public abstract class Pedido {
    InetAddress ipCliente;
    int port;

    public Pedido(InetAddress ipCliente, int port) {
        this.ipCliente = ipCliente;
        this.port = port;
    }
}
