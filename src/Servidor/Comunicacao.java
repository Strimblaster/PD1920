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


}
