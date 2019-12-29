package Servidor.Runnables;

import Comum.Pedidos.PedidoPlaylists;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Playlist;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GetPlaylistsRunnable extends RunnableBase {

    PedidoPlaylists pedidoPlaylists;

    public GetPlaylistsRunnable(Socket cliente, PedidoPlaylists pedidoPlaylists, IServer servidor) {
        super(cliente, servidor);
        this.pedidoPlaylists = pedidoPlaylists;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoPlaylists.getUtilizador();

            ArrayList<Playlist> playlists = servidor.getPlaylists(utilizador);

            String str = gson.toJson(playlists);
            byte[] bytes = str.getBytes();

            outputStream.write(bytes);
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [GetPlaylistsRunnable]: " + e.getMessage());
        }
    }
}
