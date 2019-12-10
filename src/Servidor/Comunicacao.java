package Servidor;

import Comum.Constants;
import Comum.IComunicacao;
import Comum.ServerInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao implements IComunicacao, Constants, ServerConstants {
    ServerSocket serverSocket;
    DatagramSocket dsSocket;
    ArrayList<Socket> clientes;
    ArrayList<ServerInfo> servidores;


    public Comunicacao() throws IOException {
        serverSocket = new ServerSocket(0);
        dsSocket = new DatagramSocket();
        clientes = new ArrayList<>();
        dsSocket.setSoTimeout(TIMEOUT);
    }


    public int requestServerID() throws IOException {
        String porta = Integer.toString(serverSocket.getLocalPort());
        byte[] b = porta.getBytes();
        int id;

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), SERVER_PORT_DS);
        dsSocket.send(p);
        dsSocket.receive(p);
        id = Integer.parseInt(new String(p.getData(), 0, p.getLength()));

        //multicast
//        dsSocket.receive(p);
//        Gson gson = new Gson();
//        String arrayList = new String(p.getData(), 0, p.getLength());
//
//        this.servidores = gson.fromJson(arrayList, ArrayList.class);

        return id;
    }


}
