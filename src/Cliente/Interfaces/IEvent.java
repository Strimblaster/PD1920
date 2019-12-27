package Cliente.Interfaces;

import Comum.Song;

import java.io.File;

public interface IEvent {
    void songUploaded(String nome);
    void songDownload(byte[] file, File fileToReceive, Song song);
}
