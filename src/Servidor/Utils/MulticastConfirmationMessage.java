package Servidor.Utils;

import Comum.Pedidos.Pedido;
import Comum.ServerInfo;

public class MulticastConfirmationMessage {

    ServerInfo sender;
    ServerInfo receiver;
    boolean sucess;

    public MulticastConfirmationMessage(ServerInfo sender, ServerInfo receiver, boolean sucess) {
        this.sender = sender;
        this.receiver = receiver;
        this.sucess = sucess;
    }

    public ServerInfo getSender() {
        return sender;
    }

    public void setSender(ServerInfo sender) {
        this.sender = sender;
    }

    public ServerInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(ServerInfo receiver) {
        this.receiver = receiver;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    @Override
    public String toString() {
        return "Confimation:"+
                "\nsender= " + sender +
                "\nreceiver= " + receiver +
                "\nsucess= " + sucess;
    }
}
