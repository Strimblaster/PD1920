package Cliente;

import Cliente.Interfaces.IComunicacaoCliente;
import Comum.Exceptions.InvalidServerException;
import Comum.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;

public class Comunicacao implements IComunicacaoCliente, Constants {
    Socket tcpSocket;
    DatagramSocket udpSocket;

    public Comunicacao() throws SocketException {
        udpSocket = new DatagramSocket();
    }

    @Override
    public ServerInfo getServerInfo() throws IOException, InvalidServerException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] incommingData = new byte[PKT_SIZE];


        PrintWriter out = new PrintWriter(byteArrayOutputStream,true);
        out.println("Olá DS!\n");
        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, InetAddress.getByName(IP_DS), CLIENT_PORT_DS);
        udpSocket.send(datagramPacket);

        datagramPacket = new DatagramPacket(incommingData, incommingData.length);
        udpSocket.receive(datagramPacket);


        Gson gson = new Gson();
        String jsonServerInfo = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        ServerInfo server = gson.fromJson(jsonServerInfo, ServerInfo.class);

        if(server == null)
            throw new InvalidServerException();

        return server;
    }

}
