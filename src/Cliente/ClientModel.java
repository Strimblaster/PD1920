package Cliente;

import Comum.Constants;
import Comum.ServerInfo;
import Comum.Utilizador;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


class ClientModel implements Constants {

    private ServerInfo server;
    private Utilizador utilizador;

    void sendMessageDS() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] incommingData = new byte[PKT_SIZE];


        //.... Tou só a a ver O.O
        PrintWriter out = new PrintWriter(byteArrayOutputStream,true);
        out.println("Olá DS!\n");
<<<<<<< HEAD
        out.close();
        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, InetAddress.getByName(HOST), PORT);
=======
        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.toByteArray().length, InetAddress.getByName(IP_DS), CLIENT_PORT_DS);
>>>>>>> 0e9c46fa36e24b4d53b2334785679feb7d7ec5a1
        datagramSocket.send(datagramPacket);

        System.out.println("Mensagem enviada ao DS!");
        datagramPacket = new DatagramPacket(incommingData, incommingData.length);
        datagramSocket.receive(datagramPacket);
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
