package Servidor.Utils;

import Comum.ServerInfo;

public class MulticastConfirmationMessage {

    ServerInfo sender;
    boolean sucess;

    public MulticastConfirmationMessage(ServerInfo sender, boolean sucess) {
        this.sender = sender;
        this.sucess = sucess;
    }

    public ServerInfo getSender() {
        return sender;
    }

    public void setSender(ServerInfo sender) {
        this.sender = sender;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    @Override
    public String toString() {
        return "Confirmation:"+
                "\nsender= " + sender +
                "\nsucess= " + sucess;
    }
}
