package Servidor.Utils;

import Comum.Pedidos.Pedido;
import Comum.ServerInfo;
import Servidor.Servidor;
import com.google.gson.annotations.Expose;

public class MulticastMessage {

    private ServerInfo sender;
    private ServerInfo receiver; //null se for para todos
    @Expose
    private TipoMensagemMulticast tipoMensagem;

    private Pedido pedido;
    @Expose
    String file;

    public MulticastMessage(ServerInfo sender, ServerInfo receiver, Pedido pedido, String file, TipoMensagemMulticast tipoMensagem) {
        this.sender = sender;
        this.receiver = receiver;
        this.pedido = pedido;
        this.tipoMensagem = tipoMensagem;
    }

    public MulticastMessage(ServerInfo sender, Pedido pedido, String file, TipoMensagemMulticast tipoMensagem) {
        this.sender = sender;
        this.pedido = pedido;
        this.tipoMensagem = tipoMensagem;
        receiver = null;
    }

    public MulticastMessage(ServerInfo sender, TipoMensagemMulticast tipoMensagem) {
        this.sender = sender;
        this.tipoMensagem = tipoMensagem;
        receiver = null;
    }

    public void setSender(ServerInfo sender) {
        this.sender = sender;
    }

    public void setReceiver(ServerInfo receiver) {
        this.receiver = receiver;
    }

    public void setTipoMensagem(TipoMensagemMulticast tipoMensagem) {
        this.tipoMensagem = tipoMensagem;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ServerInfo getSender() {
        return sender;
    }

    public ServerInfo getReceiver() {
        return receiver;
    }

    public TipoMensagemMulticast getTipoMensagem() {
        return tipoMensagem;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getFile() {
        return file;
    }
}
