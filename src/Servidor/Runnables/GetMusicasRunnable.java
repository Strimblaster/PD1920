package Servidor.Runnables;

import Comum.Pedidos.PedidoMusicas;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Song;
import Comum.Utilizador;
import Servidor.Interfaces.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GetMusicasRunnable extends RunnableBase {

    private PedidoMusicas pedidoMusicas;

    public GetMusicasRunnable(Socket cliente, PedidoMusicas pedidoMusicas, Observable servidor) {
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


            outputStream.write(bytes);
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [GetMusicasRunnable]: " + e.getMessage());
        }
    }
}
