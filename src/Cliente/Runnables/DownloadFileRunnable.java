package Cliente.Runnables;

import Cliente.Comunicacao;
import Cliente.Interfaces.Listener;
import Comum.Pedidos.PedidoDownloadFile;
import Comum.Song;

import java.io.*;
import java.net.Socket;

import static Comum.Constants.PKT_SIZE;

public class DownloadFileRunnable implements Runnable {

    private Socket server;
    private PedidoDownloadFile pedido;
    private Listener event;
    private File clientMusicDir;
    private Comunicacao comunicacao;

    public DownloadFileRunnable(Socket server, PedidoDownloadFile pedido, Listener event, File clientMusicDir, Comunicacao comunicacao) {
        this.server = server;
        this.pedido = pedido;
        this.event = event;
        this.clientMusicDir = clientMusicDir;
        this.comunicacao = comunicacao;
    }

    @Override
    public void run() {
        Song song = pedido.getMusica();
        try {
            File pathToSave = new File(clientMusicDir.getAbsolutePath()+File.separator+song.getFilename());

            InputStream inputStream = server.getInputStream();
            byte[] file = new byte[0];
            byte[] buffer = new byte[PKT_SIZE];
            int nRead;

            try{
                while((nRead=inputStream.read(buffer))!=-1) {
                    byte[] temp = new byte[file.length + nRead];
                    System.arraycopy(file, 0, temp, 0, file.length);
                    System.arraycopy(buffer, 0, temp, file.length, nRead);
                    file = temp;
                }
            }catch (IOException e){
                comunicacao.disconnected(pedido);
            }

            server.close();
            event.songDownloaded(file, pathToSave, song.getNome());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
