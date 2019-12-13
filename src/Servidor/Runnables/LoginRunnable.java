package Servidor.Runnables;

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

public class LoginRunnable implements Runnable  {

    private Socket cliente;
    private PedidoLogin pedidoLogin;
    private IServer servidor;

    public LoginRunnable(Socket cliente, PedidoLogin pedidoLogin, IServer servidor) {
        this.cliente = cliente;
        this.pedidoLogin = pedidoLogin;
        this.servidor = servidor;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            System.out.println("[INFO] - [Login]: Pedido de login de " + cliente.getInetAddress().getHostName() + ":" + cliente.getPort() + " em processamento");
            Utilizador utilizador = pedidoLogin.getUtilizador();
            Resposta resposta = servidor.login(utilizador.getName(), utilizador.getPassword());
            String str = gson.toJson(resposta);

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [LoginThread]: " + e.getMessage());
        }

    }
}
