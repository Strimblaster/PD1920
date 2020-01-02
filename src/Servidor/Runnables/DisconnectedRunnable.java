package Servidor.Runnables;

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

public class DisconnectedRunnable extends RunnableBase {

    private Pedido pedido;

    public DisconnectedRunnable(Socket cliente, Pedido pedido, IServer servidor) {
        super(cliente, servidor);
        this.pedido = pedido;
    }

    @Override
    public void run() {
        /*if(pedido instanceof PedidoUploadFile){

            Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
            try {
                InputStream inputStream = cliente.getInputStream();

                byte[] file = new byte[0];
                byte[] buffer = new byte[PKT_SIZE];
                int nRead;

                while((nRead=inputStream.read(buffer))!=-1) {
                    byte[] temp = new byte[file.length + nRead];
                    System.arraycopy(file, 0, temp, 0, file.length);
                    System.arraycopy(buffer, 0, temp, file.length, nRead);
                    file = temp;
                }
                servidor.saveSongFile(((PedidoUploadFile) pedido).getMusica(), file);

                cliente.close();
            } catch (IOException e) {
                System.out.println("[Erro] - [UploadThread]: " + e.getMessage());
            }

        }
        else if(pedido instanceof PedidoDownloadFile){
            try {
                OutputStream outputStream = cliente.getOutputStream();

                byte[] musica = servidor.downloadFile(pedido.getUtilizador(), ((PedidoDownloadFile) pedido).getMusica());
                if(musica != null)
                    outputStream.write(musica);

                cliente.close();
            } catch (IOException e) {
                System.out.println("[Erro] - [DownloadThread]: " + e.getMessage());
            }
        }*/
    }
}
