package Servidor.Runnables;

import Servidor.Interfaces.Observable;

import java.net.Socket;

public abstract class RunnableBase implements Runnable {
    protected Socket cliente;
    protected Observable servidor;

    public RunnableBase(Socket cliente, Observable servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }
}
