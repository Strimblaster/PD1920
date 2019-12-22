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

public class UploadFileRunnable implements Runnable {

    private Socket cliente;
    private PedidoUploadFile pedidoUploadFile;
    private IServer servidor;

    public UploadFileRunnable (Socket cliente, PedidoUploadFile pedidoUploadFile, IServer servidor) {
        this.cliente = cliente;
        this.pedidoUploadFile = pedidoUploadFile;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            InputStream inputStream = cliente.getInputStream();
            OutputStream outputStream = cliente.getOutputStream();
            System.out.println("[INFO] - [UploadFile]: Novo pedido de Upload: " + cliente.getInetAddress().getHostName() + ":" + cliente.getPort());

            Resposta resposta;
            try {
                resposta = servidor.uploadFile(pedidoUploadFile.getUtilizador(), pedidoUploadFile.getMusica());
            } catch (InvalidSongDescriptionException e) {
                resposta = new Resposta(pedidoUploadFile, false, e.getMessage(), TipoExcecao.InvalidSongDescription, e);
            }
            String str = gson.toJson(resposta);
            outputStream.write(str.getBytes());

            byte[] file = new byte[0];
            byte[] buffer = new byte[PKT_SIZE];
            int nRead;

            while((nRead=inputStream.read(buffer))!=-1) {
                byte[] temp = new byte[file.length + nRead];
                System.arraycopy(file, 0, temp, 0, file.length);
                System.arraycopy(buffer, 0, temp, file.length, nRead);
                file = temp;
            }

            servidor.addNewSong(pedidoUploadFile.getMusica(), file);

            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [LoginThread]: " + e.getMessage());
        }

    }
}
