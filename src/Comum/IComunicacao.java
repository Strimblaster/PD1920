package Comum;

import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;

public interface IComunicacao {

    Resposta login(PedidoLogin pedidoLogin);
}
