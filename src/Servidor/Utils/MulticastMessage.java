package Servidor.Utils;

import Comum.Pedidos.Pedido;
import Comum.ServerInfo;
import Servidor.Servidor;

public class MulticastMessage {

    ServerInfo sender;
    ServerInfo receiver; //null se for para todos
    TipoMensagemMulticast tipoMensagem;

    Pedido pedido;
    byte[]

    public MulticastMessage(ServerInfo sender, ServerInfo receiver, Pedido pedido) {
        this.sender = sender;
        this.receiver = receiver;
        this.pedido = pedido;
    }

    public MulticastMessage(ServerInfo sender, Pedido pedido) {
        this.sender = sender;
        this.pedido = pedido;
    }

    public MulticastMessage(ServerInfo sender) {
        this.sender = sender;
    }
}
