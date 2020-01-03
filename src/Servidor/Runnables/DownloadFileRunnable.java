package Servidor.Runnables;

import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Servidor.Interfaces.IServer;
import Servidor.Utils.ThreadMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static Comum.Constants.PKT_SIZE;

public class DownloadFileRunnable extends RunnableBase {

    private final ThreadMode threadMode;
    private PedidoDownloadFile pedidoDownloadFile;

    public DownloadFileRunnable(Socket cliente, PedidoDownloadFile pedidoDownloadFile, IServer servidor, ThreadMode threadMode) {
        super(cliente, servidor);
        this.pedidoDownloadFile = pedidoDownloadFile;
        this.threadMode = threadMode;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        try {
            OutputStream outputStream = cliente.getOutputStream();

            if(threadMode == ThreadMode.Disconnect){
                Resposta resposta = new Resposta(pedidoDownloadFile, true, "OK");
                byte[] bytes = gson.toJson(resposta).getBytes();
                outputStream.write(bytes);
            }

            byte[] musica = servidor.downloadFile(pedidoDownloadFile.getUtilizador(), pedidoDownloadFile.getMusica());
            if(musica != null)
                outputStream.write(musica);

            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [DownloadThread]: " + e.getMessage());
        }

    }
}
