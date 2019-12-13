package Comum;

import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;

public interface IComunicacao {

    Resposta login(String username, String password);
    Resposta signUp(String username, String password);

}
