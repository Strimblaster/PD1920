package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoExcecao;
import com.google.gson.annotations.Expose;

public class Resposta {

    private Pedido pedido;
    @Expose
    private boolean sucess;
    @Expose
    private String info;
    @Expose
    private TipoExcecao tipoExcecao;

    private Exception exception;

    public Resposta(Pedido pedido, boolean sucess, String info) {
        this.pedido = pedido;
        this.sucess = sucess;
        this.info = info;
        exception = null;
    }

    public Resposta(Pedido pedido, boolean sucess, String info, TipoExcecao tipoExcecao, Exception exception) {
        this.pedido = pedido;
        this.sucess = sucess;
        this.info = info;
        this.tipoExcecao = tipoExcecao;
        this.exception = exception;
    }
    public TipoExcecao getTipoExcecao() {
        return tipoExcecao;
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

    public String getInfo() {
        return info;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {

        return "Resposta:\nUtilizador: " +  pedido.getUtilizador().getName() + ", Tipo: " + pedido.getTipo().toString() + ", Sucesso: " + sucess
                + ", Info: " + info +  (exception != null? ("\nExceção: " + exception.getMessage()): "");
    }
}
