package Servidor.Utils;

import Comum.Pedidos.Pedido;
import Comum.ServerInfo;
import Servidor.Servidor;
import com.google.gson.annotations.Expose;

public class MulticastMessage {

    private ServerInfo sender;
    private ServerInfo receiver; //null se for para todos


    private Pedido pedido;
    @Expose
    String encodedfile;

    public MulticastMessage(ServerInfo sender, ServerInfo receiver, Pedido pedido, String file) {
        this.sender = sender;
        this.receiver = receiver;
        this.pedido = pedido;
        this.encodedfile = file;
    }

    public MulticastMessage(ServerInfo sender, Pedido pedido, String file) {
        this.sender = sender;
        this.pedido = pedido;
        this.encodedfile = file;
        receiver = null;
    }

    public MulticastMessage(ServerInfo sender, Pedido pedido) {
        this.sender = sender;
        this.pedido = pedido;
        receiver = null;
    }

    public MulticastMessage(ServerInfo sender) {
        this.sender = sender;
        receiver = null;
    }

    public void setSender(ServerInfo sender) {
        this.sender = sender;
    }

    public void setReceiver(ServerInfo receiver) {
        this.receiver = receiver;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void setFile(String file) {
        this.encodedfile = file;
    }

    public ServerInfo getSender() {
        return sender;
    }

    public ServerInfo getReceiver() {
        return receiver;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getFile() {
        return encodedfile;
    }
}
