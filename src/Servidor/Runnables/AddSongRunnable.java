package Servidor.Runnables;

import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoAddSong;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class AddSongRunnable extends RunnableBase implements Runnable {

    PedidoAddSong pedidoAddSong;

    public AddSongRunnable(Socket cliente, PedidoAddSong pedidoAddSong, Observable servidor) {
        super(cliente, servidor);
        this.pedidoAddSong = pedidoAddSong;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoAddSong.getUtilizador();
            Resposta resposta;
            boolean sucess;

            try {
                sucess = servidor.addSong(utilizador, pedidoAddSong.getPlaylist(), pedidoAddSong.getSong());
                if(sucess)
                    resposta = new Resposta(pedidoAddSong, true, "Musica Adicionada");
                else
                    resposta = new Resposta(pedidoAddSong, false, "Erro no servidor");
            } catch (InvalidPlaylistNameException e) {
                resposta = new Resposta(pedidoAddSong, false, e.getMessage(), TipoExcecao.InvalidPlaylistNameException ,e);
            } catch (InvalidSongDescriptionException e) {
                resposta = new Resposta(pedidoAddSong, false, e.getMessage(), TipoExcecao.InvalidSongDescription ,e);
            }

            String str = gson.toJson(resposta);
            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [AddSong]: " + e.getMessage());
        }
    }
}
