package Cliente.Interfaces;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoUploadFile;
import Comum.Song;

import java.io.File;

public interface Listener {
    void songUploaded(String nome);
    void songDownloaded(byte[] file, File fileToReceive, String nomeDaMusica);

}
