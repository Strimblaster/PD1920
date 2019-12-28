package Cliente.Interfaces;

import Comum.Song;

import java.io.File;

public interface IEvent {
    void songUploaded(String nome);
    void songDownloaded(byte[] file, File fileToReceive, String nomeDaMusica);
}
