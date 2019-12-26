package Comum.Pedidos;

import Comum.Pedidos.Enums.TipoPedido;
import Comum.Utilizador;

public abstract class Pedido  {
    private Utilizador utilizador;
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
