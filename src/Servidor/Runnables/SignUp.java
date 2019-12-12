package Servidor.Runnables;

import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.Resposta;
import Servidor.Interfaces.IComunicacaoServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SignUp extends Thread {

    private Socket cliente;
    private PedidoSignUp pedidoSignUp;
    private IComunicacaoServer servidor;

    public SignUp(Socket cliente, PedidoSignUp pedidoSignUp, IComunicacaoServer servidor) {
        this.cliente = cliente;
        this.pedidoSignUp = pedidoSignUp;
        this.servidor = servidor;
    }


    @Override
    public void run() {

        Gson gson = new Gson();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            System.out.println("[INFO] - [SignUp]: Pedido de signUp de " + cliente.getInetAddress().getHostName() + ":" + cliente.getPort() + " em processamento");

            Resposta resposta = servidor.signUp(pedidoSignUp);
            String str = gson.toJson(resposta);

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [LoginThread]: " + e.getMessage());
        }

    }

}
