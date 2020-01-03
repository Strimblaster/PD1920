package Servidor.Runnables;

import Comum.Pedidos.*;
import Servidor.Interfaces.Observable;

import java.net.Socket;

public class DisconnectedRunnable extends RunnableBase {

    private Pedido pedido;

    public DisconnectedRunnable(Socket cliente, Pedido pedido, Observable servidor) {
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
