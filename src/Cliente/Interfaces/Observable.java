package Cliente.Interfaces;

import Comum.Exceptions.InvalidServerException;
import Comum.IComunicacao;
import Comum.ServerInfo;

import java.io.File;
import java.io.IOException;

public interface Observable extends IComunicacao {

    ServerInfo getServerInfo() throws IOException, InvalidServerException;
    void setMusicDir(File dir);
}
