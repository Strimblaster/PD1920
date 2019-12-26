package Servidor.Runnables;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SignUpRunnable extends RunnableBase implements Runnable {

    private PedidoSignUp pedidoSignUp;

    public SignUpRunnable(Socket cliente, PedidoSignUp pedidoSignUp, IServer servidor) {
        super(cliente, servidor);
        this.pedidoSignUp = pedidoSignUp;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoSignUp.getUtilizador();
            Resposta resposta = null;
            try {
                resposta = servidor.signUp(utilizador.getName(), utilizador.getPassword());
            } catch (InvalidUsernameException e) {
                resposta = new Resposta(pedidoSignUp, false, e.getMessage(), TipoExcecao.InvalidUsername, e);
            } catch (InvalidPasswordException e) {
                resposta = new Resposta(pedidoSignUp, false, e.getMessage(), TipoExcecao.InvalidPassword, e);
            }
            String str = gson.toJson(resposta);
            byte[] bytes = str.getBytes();

            System.out.println("DEBUG: " + bytes.length + " bytes enviados (Não apagar isto por enquanto pls)");

            outputStream.write(bytes);
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [SignUp]: " + e.getMessage());
        }

    }

}
