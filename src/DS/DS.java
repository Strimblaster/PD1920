package DS;

import Comum.ServerInfo;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DS{
    public static final int SERVIDOR_PORT_DS = 5000;
    public static final int CLIENTE_PORT_DS = 5001;
    public static final int TAM_BYTE_ARRAY = 256;
    private int proximoServidor;

    ArrayList<ServerInfo> servidores;
    DatagramSocket servidorDatagramSocket;
    DatagramSocket clienteDatagramSocket;


    public DS() throws SocketException {
        servidorDatagramSocket = new DatagramSocket(SERVIDOR_PORT_DS);
        clienteDatagramSocket = new DatagramSocket(CLIENTE_PORT_DS);
        proximoServidor = 0;
        servidores = new ArrayList<>();
    }

    public int getProximoServidor(){
        return this.proximoServidor;
    }


    public void incrementaProximoServidor(){
        if(proximoServidor == servidores.size()-1)
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
