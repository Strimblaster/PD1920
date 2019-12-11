package Servidor;

import Comum.Constants;
import Servidor.Interfaces.IComunicacaoServer;
import Servidor.Interfaces.IEvent;
import Servidor.Interfaces.ServerConstants;
import Servidor.Runnables.PingRunnable;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao implements IEvent, Constants, ServerConstants {
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private ArrayList<Socket> clientes;
    private IComunicacaoServer server;
    private Thread pingThread;



    public Comunicacao(Servidor servidor) throws IOException {
        serverSocket = new ServerSocket(0);
        datagramSocket = new DatagramSocket();
        clientes = new ArrayList<>();
        datagramSocket.setSoTimeout(TIMEOUT);

        server = servidor;
        servidor.setListener(this);
    }

    private int requestServerID() throws IOException {
        String porta = Integer.toString(serverSocket.getLocalPort());
        byte[] b = porta.getBytes();
        int id;

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), SERVER_PORT_DS);
        datagramSocket.send(p);
        datagramSocket.receive(p);
        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));

        //multicast
//        dsSocket.receive(p);
//        Gson gson = new Gson();
//        String arrayList = new String(p.getData(), 0, p.getLength());
//
//        this.servidores = gson.fromJson(arrayList, ArrayList.class);

        return id;
    }


    @Override
    public void serverReady() {
        pingThread = new Thread(new PingRunnable(datagramSocket));
        pingThread.start();
    }

    @Override
    public void needID() throws IOException {
        server.setID(requestServerID());
    }
}
