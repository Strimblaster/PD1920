package Servidor.Runnables;

import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoNewPlaylist;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class NewPlaylistRunnable extends RunnableBase {

    PedidoNewPlaylist pedidoNewPlaylist;

    public NewPlaylistRunnable(Socket cliente, PedidoNewPlaylist pedidoNewPlaylist, Observable servidor) {
        super(cliente, servidor);
        this.pedidoNewPlaylist = pedidoNewPlaylist;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoNewPlaylist.getUtilizador();
            Resposta resposta = null;
            try {
                boolean sucess = servidor.newPlaylist(utilizador, pedidoNewPlaylist.getNome());
                if(sucess)
                    resposta = new Resposta(pedidoNewPlaylist, true, "Playlist criada");
                else
                    resposta = new Resposta(pedidoNewPlaylist, false, "Erro no servidor");
            } catch (InvalidPlaylistNameException e) {
                resposta = new Resposta(pedidoNewPlaylist, false, e.getMessage(), TipoExcecao.InvalidPlaylistNameException, e);
            }
            String str = gson.toJson(resposta);

            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [NewPlaylist]: " + e.getMessage());
        }
    }
}
