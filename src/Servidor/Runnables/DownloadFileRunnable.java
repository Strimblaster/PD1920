package Servidor.Runnables;

import Comum.Pedidos.*;
import Servidor.Interfaces.Observable;
import Servidor.Utils.ThreadMode;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class DownloadFileRunnable extends RunnableBase {

    private final ThreadMode threadMode;
    private PedidoDownloadFile pedidoDownloadFile;

    public DownloadFileRunnable(Socket cliente, PedidoDownloadFile pedidoDownloadFile, Observable servidor, ThreadMode threadMode) {
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
