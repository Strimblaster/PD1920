package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;
import com.google.gson.annotations.Expose;

public abstract class Pedido  {
    @Expose
    private Utilizador utilizador;
    @Expose
    private TipoPedido tipo; // Este tipo é necessario para depois ao deserializar de JSON sabermos em que classe temos que deserializar;

    public Pedido(Utilizador cliente, TipoPedido tipo) {
        this.utilizador = cliente;
        this.tipo = tipo;
    }

    public Pedido(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador;
    }
}
