package Servidor.Runnables;

import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Pedidos.Enums.TipoExcecao;
import Comum.Pedidos.*;
import Comum.Pedidos.Serializers.ExceptionSerializer;
import Servidor.Interfaces.Observable;
import Servidor.Utils.ThreadMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static Comum.Constants.PKT_SIZE;

public class UploadFileRunnable extends RunnableBase {

    private final ThreadMode threadMode;
    private PedidoUploadFile pedidoUploadFile;

    public UploadFileRunnable(Socket cliente, PedidoUploadFile pedidoUploadFile, Observable servidor, ThreadMode disconnect) {
        super(cliente, servidor);
        this.pedidoUploadFile = pedidoUploadFile;
        this.threadMode = disconnect;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class, new ExceptionSerializer()).create();
        try {
            InputStream inputStream = cliente.getInputStream();
            OutputStream outputStream = cliente.getOutputStream();

            Resposta resposta;

            if(threadMode == ThreadMode.Normal){
                try {
                    String path = servidor.uploadFile(pedidoUploadFile.getUtilizador(), pedidoUploadFile.getMusica());
                    if (path != null) {
                        pedidoUploadFile.getMusica().setFilename(path);
                        resposta = new Resposta(pedidoUploadFile, true, "OK");
                    }
                    else
                        resposta = new Resposta(pedidoUploadFile, false, "Erro no servidor");

                }catch (InvalidSongDescriptionException e) {
                    resposta = new Resposta(pedidoUploadFile, false, e.getMessage(), TipoExcecao.InvalidSongDescription, e);
                }
            } else{
                String path = servidor.checkSong(pedidoUploadFile.getMusica());
                if(path == null)
                    resposta = new Resposta(pedidoUploadFile, false, "A musica não existe no servidor");
                else
                    resposta = new Resposta(pedidoUploadFile, true, "OK");
            }

            String str = gson.toJson(resposta);
            outputStream.write(str.getBytes());
            if(!resposta.isSucess()){
                cliente.close();
                return;
            }

            byte[] file = new byte[0];
            byte[] buffer = new byte[PKT_SIZE];
            int nRead;

            while((nRead=inputStream.read(buffer))!=-1) {
                byte[] temp = new byte[file.length + nRead];
                System.arraycopy(file, 0, temp, 0, file.length);
                System.arraycopy(buffer, 0, temp, file.length, nRead);
                file = temp;
            }
            servidor.saveSongFile_Full(pedidoUploadFile.getUtilizador(),pedidoUploadFile.getMusica(), file);

            cliente.close();
        } catch (IOException e) {
            System.out.println("[Erro] - [UploadThread]: " + e.getMessage());
        }

    }
}
