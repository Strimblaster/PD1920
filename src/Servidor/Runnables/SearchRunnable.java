package Servidor.Runnables;

import Comum.FilteredResult;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.PedidoSearch;
import Comum.Pedidos.Resposta;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Comum.Utilizador;
import Servidor.Interfaces.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SearchRunnable extends RunnableBase {

    PedidoSearch pedidoSearch;

    public SearchRunnable(Socket cliente, PedidoSearch pedidoSearch, Observable servidor) {
        super(cliente, servidor);
        this.pedidoSearch = pedidoSearch;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            OutputStream outputStream = cliente.getOutputStream();

            Utilizador utilizador = pedidoSearch.getUtilizador();
            Resposta resposta = new Resposta(pedidoSearch, true, "OK");
            try {
                FilteredResult filteredResult = servidor.search(
                        utilizador,
                        pedidoSearch.isSongs(),
                        pedidoSearch.isPlaylists(),
                        pedidoSearch.getNome(),
                        pedidoSearch.getAlbum(),
                        pedidoSearch.getGenero(),
                        pedidoSearch.getAno(),
                        pedidoSearch.getDuracao());

                pedidoSearch.setFilteredResult(filteredResult);

            } catch (Exception e) {
                resposta = new Resposta(pedidoSearch, false, e.getMessage(), TipoExcecao.Exception, e);
                System.out.println("[ERRO] - [SearchRunnable]: Erro" + e.getMessage());
            }

            String str = gson.toJson(resposta);
            byte[] bytes = str.getBytes();

            System.out.println("DEBUG: " + bytes.length + " bytes enviados (Não apagar isto por enquanto pls)");

            outputStream.write(bytes);
            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [SignUp]: " + e.getMessage());
        }
    }
}
