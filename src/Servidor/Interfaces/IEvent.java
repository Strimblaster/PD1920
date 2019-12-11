package Servidor.Interfaces;

import java.io.IOException;

public interface IEvent {
    void serverReady();

    void needID() throws IOException;

    // void newMusic(Music x); multicast

}
