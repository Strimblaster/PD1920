package Servidor.Runnables;

import Cliente.Interfaces.IEvent;
import Comum.Pedidos.PedidoEditPlaylist;
import Comum.Pedidos.PedidoEditSong;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class EditPlaylistRunnable extends RunnableBase implements Runnable {

    PedidoEditPlaylist pedidoEditPlaylist;

    public EditPlaylistRunnable(Socket cliente, PedidoEditPlaylist pedidoEditPlaylist, IServer servidor) {
        super(cliente, servidor);
        this.pedidoEditPlaylist = pedidoEditPlaylist;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoEditPlaylist.getUtilizador();
            Resposta resposta;
            boolean sucess;

            sucess = servidor.editPlaylist(utilizador, pedidoEditPlaylist.getPlaylist());
            if(sucess)
                resposta = new Resposta(pedidoEditPlaylist, true, "Playlist alterada");
            else
                resposta = new Resposta(pedidoEditPlaylist, false, "Erro no servidor");

            String str = gson.toJson(resposta);
            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [EditPlaylist]: " + e.getMessage());
        }
    }
}
