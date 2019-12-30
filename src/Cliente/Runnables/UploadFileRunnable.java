package Cliente.Runnables;

import Cliente.Comunicacao;
import Cliente.Interfaces.IEvent;
import Comum.Pedidos.PedidoUploadFile;
import Comum.Song;

import java.io.*;
import java.net.Socket;

import static Comum.Constants.PKT_SIZE;

public class UploadFileRunnable implements Runnable {

    private Socket server;
    private PedidoUploadFile pedido;
    private IEvent event;
    private File clientMusicDir;
    private String filenameMusicID;  //Para guardar o ficheiro na pasta do cliente com o id da musica
    private Comunicacao comunicacao;

    public UploadFileRunnable(Socket server, PedidoUploadFile pedido, IEvent event, File clientMusicDir, String filename, Comunicacao comunicacao) {
        this.server = server;
        this.pedido = pedido;
        this.event = event;
        this.clientMusicDir = clientMusicDir;
        this.filenameMusicID = filename;
        this.comunicacao = comunicacao;
    }

    @Override
    public void run() {
        Song song = pedido.getMusica();
        try {

            File fileToSend = new File(song.getFilename());
            //Guardar o ficheiro tambem na pasta temporaria de cada cliente para nao dar upload e depois ter que dar download do ficheiro que acabou de dar upload!
            File fileToCache = new File(clientMusicDir.getAbsolutePath() + File.separator + filenameMusicID);
            FileOutputStream fileOutputStream = new FileOutputStream(fileToCache);

            if(!fileToSend.exists() || !fileToSend.canWrite())
                System.out.println("Erro ao abrir o ficheiro");

            OutputStream outputStream = server.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(fileToSend);
            byte[] bytes = new byte[PKT_SIZE];
            int nread;

            while((nread = fileInputStream.read(bytes)) != -1){
                try{
                    outputStream.write(bytes, 0, nread);
                } catch (IOException e){
                    comunicacao.disconnected(pedido);
                }
                fileOutputStream.write(bytes, 0, nread);

                outputStream.flush();
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            server.close();

            event.songUploaded(song.getNome());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
