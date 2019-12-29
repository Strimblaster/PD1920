package Servidor.Runnables;

import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Servidor.Interfaces.IServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static Comum.Constants.PKT_SIZE;

public class DownloadFileRunnable extends RunnableBase {

    private PedidoDownloadFile pedidoDownloadFile;

    public DownloadFileRunnable(Socket cliente, PedidoDownloadFile pedidoDownloadFile, IServer servidor) {
        super(cliente, servidor);
        this.pedidoDownloadFile = pedidoDownloadFile;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = cliente.getOutputStream();

            byte[] musica = servidor.downloadFile(pedidoDownloadFile.getUtilizador(), pedidoDownloadFile.getMusica());
            if(musica != null)
                outputStream.write(musica);

            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [DownloadThread]: " + e.getMessage());
        }

    }
}
