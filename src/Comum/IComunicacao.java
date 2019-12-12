package Comum;

import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;

public interface IComunicacao {

    Resposta login(PedidoLogin pedidoLogin);
    Resposta signUp(PedidoSignUp pedidoSignUp);
}
