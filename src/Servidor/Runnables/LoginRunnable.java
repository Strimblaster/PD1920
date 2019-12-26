package Servidor.Runnables;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class LoginRunnable extends RunnableBase implements Runnable  {

    private PedidoLogin pedidoLogin;

    public LoginRunnable(Socket cliente, PedidoLogin pedidoLogin, IServer servidor) {
        super(cliente, servidor);
        this.pedidoLogin = pedidoLogin;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoLogin.getUtilizador();
            Resposta resposta = null;
            try {
                resposta = servidor.login(utilizador.getName(), utilizador.getPassword());
            } catch (InvalidUsernameException e) {
                resposta = new Resposta(pedidoLogin, false, e.getMessage(), TipoExcecao.InvalidUsername, e);
            } catch (InvalidPasswordException e) {
                resposta = new Resposta(pedidoLogin, false, e.getMessage(), TipoExcecao.InvalidPassword, e);
            }
            String str = gson.toJson(resposta);

            System.out.println("DEBUG: " + str.getBytes().length + " bytes enviados (Não apagar isto por enquanto pls)");

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [Login]: " + e.getMessage());
        }

    }
}
