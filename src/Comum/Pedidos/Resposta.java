package Comum.Pedidos;

import com.google.gson.annotations.Expose;

import java.sql.SQLException;

public class Resposta {

    private Pedido pedido;
    @Expose
    private boolean sucess;
    @Expose
    private String info;
    @Expose
    private Exception exception;

    public Resposta(Pedido pedido, boolean sucess, String info) {
        this.pedido = pedido;
        this.sucess = sucess;
        this.info = info;
        exception = null;
    }

    public Resposta(boolean sucess, String info) {
        this.sucess = sucess;
        this.info = info;
        exception = null;
    }

    public Resposta(boolean sucess, String info, Exception exception) {
        this.sucess = sucess;
        this.info = info;
        this.exception = exception;
    }

    public Resposta(PedidoLogin pedido, boolean sucess, String info, Exception exception) {
        this.pedido = pedido;
        this.sucess = sucess;
        this.info = info;
        this.exception = exception;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {

        return "Resposta:\nUtilizador: " +  pedido.getUtilizador().getName() + ", Tipo: " + pedido.getTipo().toString() + ", Sucesso: " + sucess
                + ", Info: " + info +  (exception != null? ("\nExceção: " + exception.getMessage()): "");
    }
}
