package Servidor.Runnables;

import Comum.Pedidos.PedidoMusicas;
import Servidor.Interfaces.IServer;

import java.net.Socket;

public class RunnableBase  {
    protected Socket cliente;
    protected IServer servidor;

    public RunnableBase(Socket cliente, IServer servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }
}
