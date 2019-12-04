package DS;

import Comum.Constants;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DS implements Constants {
    private int proximoServidor;

    ArrayList<ServerInfo> servidoresTCP;
    ArrayList<ServerInfo> servidoresUDP;
    DatagramSocket servidorDatagramSocket;
    DatagramSocket clienteDatagramSocket;



    public DS() throws SocketException {
        servidorDatagramSocket = new DatagramSocket(SERVER_PORT_DS);
        clienteDatagramSocket = new DatagramSocket(CLIENT_PORT_DS);
        proximoServidor = 0;
        servidoresTCP = new ArrayList<>();
        servidoresUDP = new ArrayList<>();
    }
    int getTotalServidores(){
        return servidoresTCP.size();
    }

    public int getProximoServidor(){
        return this.proximoServidor;
    }


    public void incrementaProximoServidor(){
        if(proximoServidor == servidoresTCP.size()-1)
            proximoServidor = 0;
        else
            proximoServidor++;
    }


    public static void main(String[] args) throws SocketException, InterruptedException {
        DS ds = new DS();
        Thread servidorThread = new Thread(new ServerThread(ds));
        Thread clienteThread = new Thread(new ClienteThread(ds));

        servidorThread.start();
        clienteThread.start();

        servidorThread.join();
        clienteThread.join();
    }
}
