package Cliente.Interfaces;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoUploadFile;
import Comum.Song;

import java.io.File;

public interface IEvent {
    void songUploaded(String nome);
    void songDownloaded(byte[] file, File fileToReceive, String nomeDaMusica);

    void disconnected(Pedido pedido, File cliMusicDir, String filename);
}
