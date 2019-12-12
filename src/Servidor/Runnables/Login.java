package Servidor.Runnables;

import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.Resposta;
import Servidor.Interfaces.IComunicacaoServer;
import Servidor.Servidor;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Login extends Thread {

    private Socket cliente;
    private PedidoLogin pedidoLogin;
    private IComunicacaoServer servidor;

    public Login(Socket cliente, PedidoLogin pedidoLogin, IComunicacaoServer servidor) {
        this.cliente = cliente;
        this.pedidoLogin = pedidoLogin;
        this.servidor = servidor;
    }

    @Override
    public void run() {

        Gson gson = new Gson();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            System.out.println("[INFO] - [Login]: Pedido de login de " + cliente.getInetAddress().getHostName() + ":" + cliente.getPort() + " em processamento");

            Resposta resposta = servidor.login(pedidoLogin);
            String str = gson.toJson(resposta);

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [LoginThread]: " + e.getMessage());
        }

    }
}
