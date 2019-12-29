package Servidor.Runnables;

import Cliente.Interfaces.IEvent;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Exceptions.InvalidUsernameException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoAddSong;
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

public class EditSongRunnable extends RunnableBase implements Runnable {

    PedidoEditSong pedidoEditSong;
    private IEvent event;

    public EditSongRunnable(Socket cliente, PedidoEditSong pedidoEditSong, IServer servidor) {
        super(cliente, servidor);
        this.pedidoEditSong = pedidoEditSong;
    }

    @Override
    public void run() {

        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();
            Utilizador utilizador = pedidoEditSong.getUtilizador();
            Resposta resposta;
            boolean sucess;

            sucess = servidor.editFile(utilizador, pedidoEditSong.getMusica());
            if(sucess)
                resposta = new Resposta(pedidoEditSong, true, "Musica alterada");
            else
                resposta = new Resposta(pedidoEditSong, false, "Erro no servidor");

            String str = gson.toJson(resposta);
            outputStream.write(str.getBytes());
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [EditSong]: " + e.getMessage());
        }
    }
}
