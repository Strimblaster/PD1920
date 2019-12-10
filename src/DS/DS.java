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
    DatagramSocket servidorPingDatagramSocket;
    DatagramSocket clienteDatagramSocket;



    public DS() throws SocketException {
        servidorDatagramSocket = new DatagramSocket(SERVER_PORT_DS);
        clienteDatagramSocket = new DatagramSocket(CLIENT_PORT_DS);
        servidorPingDatagramSocket = new DatagramSocket(SERVER_PORT_DS_PING);
        proximoServidor = 0;
        servidoresTCP = new ArrayList<>();
        servidoresUDP = new ArrayList<>();
    }
    int getTotalServidores(){
        return servidoresTCP.size();
    }

    public int getProximoServidor(){
        if(this.proximoServidor >= servidoresTCP.size())
            return 0;
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
        Thread servidorThread = new ServerThread(ds);
        Thread clienteThread = new ClienteThread(ds);

        Thread pingServidores = new PingThread(ds);

        servidorThread.start();
        clienteThread.start();
        pingServidores.start();

        servidorThread.join();
        clienteThread.join();
        pingServidores.join();
    }

    public int getNextID() {
        int i;
        //Nada para ver aqui
        for(i = 0; i < servidoresUDP.size() && i == servidoresUDP.get(i).getId(); i++);
        return i;
    }
}
