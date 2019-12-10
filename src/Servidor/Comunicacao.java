package Servidor;

import Comum.Constants;
import Comum.IComunicacao;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao implements IComunicacao, Constants, ServerConstants {
    ServerSocket serverSocket;
    DatagramSocket dsSocket;
    ArrayList<Socket> clientes;

    public Comunicacao() throws IOException {
        serverSocket = new ServerSocket(0);
        dsSocket = new DatagramSocket();
        clientes = new ArrayList<>();
        dsSocket.setSoTimeout(TIMEOUT);
    }


    public int requestServerID() throws IOException {
        String porta = Integer.toString(serverSocket.getLocalPort());
        byte[] b = porta.getBytes();

        DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(IP_DS), SERVER_PORT_DS);
        dsSocket.send(p);
        dsSocket.receive(p);

        return Integer.parseInt(new String(p.getData(), 0, p.getLength()));
    }


}
