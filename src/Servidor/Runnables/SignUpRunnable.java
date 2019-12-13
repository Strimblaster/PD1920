package Servidor.Runnables;

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

public class SignUpRunnable implements Runnable {

    private Socket cliente;
    private PedidoSignUp pedidoSignUp;
    private IServer servidor;

    public SignUpRunnable(Socket cliente, PedidoSignUp pedidoSignUp, IServer servidor) {
        this.cliente = cliente;
        this.pedidoSignUp = pedidoSignUp;
        this.servidor = servidor;
    }


    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            System.out.println("[INFO] - [SignUp]: Pedido de SignUp de " + cliente.getInetAddress().getHostName() + ":" + cliente.getPort());

            Utilizador utilizador = pedidoSignUp.getUtilizador();
            Resposta resposta = servidor.signUp(utilizador.getName(), utilizador.getPassword());
            String str = gson.toJson(resposta);

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [Thread]: " + e.getMessage());
        }

    }

}
