package Cliente;

import Comum.ServerInfo;
import Comum.Utilizador;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


class ClientModel {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;
    private static final int TIMEOUT = 5;
    private static final int BYTESIZE = 50000;
    private ServerInfo server;
    private Utilizador utilizador;

    void sendMessageDS() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] incommingData = new byte[BYTESIZE];


        PrintWriter out = new PrintWriter(byteArrayOutputStream,true);
        out.println("Olá DS!\n");
//        out.close();
        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, InetAddress.getByName(HOST), PORT);
        datagramSocket.send(datagramPacket);

        System.out.println("Mensagem enviada ao DS!");
        datagramPacket = new DatagramPacket(incommingData, incommingData.length);
        datagramSocket.receive(datagramPacket);
        System.out.println("Informação recebida");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(datagramPacket.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try {
            server = (ServerInfo) objectInputStream.readObject();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        System.out.println("Servidor Recebido!");
        if(server == null){
            System.out.println("Não existe servidor ainda!");
        }else {
            System.out.println("IP do servidor: " + server.getIp());
        }
        datagramSocket.close();
    }
}
