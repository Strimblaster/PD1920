package Servidor.Runnables;

import Servidor.Interfaces.IServer;

import java.net.Socket;

public abstract class RunnableBase implements Runnable {
    protected Socket cliente;
    protected IServer servidor;

    public RunnableBase(Socket cliente, IServer servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }
}
