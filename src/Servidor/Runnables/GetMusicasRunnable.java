package Servidor.Runnables;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoLogin;
import Comum.Pedidos.PedidoMusicas;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Song;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GetMusicasRunnable extends RunnableBase implements Runnable {

    private PedidoMusicas pedidoMusicas;

    public GetMusicasRunnable(Socket cliente, PedidoMusicas pedidoMusicas, IServer servidor) {
        super(cliente, servidor);
        this.pedidoMusicas = pedidoMusicas;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoMusicas.getUtilizador();

            ArrayList<Song> songs = servidor.getMusicas(utilizador);

            String str = gson.toJson(songs);
            byte[] bytes = str.getBytes();

            System.out.println("DEBUG: " + bytes.length + " bytes enviados (Não apagar isto por enquanto pls)");

            outputStream.write(bytes);
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [GetMusicasRunnable]: " + e.getMessage());
        }
    }
}
