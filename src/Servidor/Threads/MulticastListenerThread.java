package Servidor.Threads;

import Comum.ServerInfo;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.ServerConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class MulticastListenerThread extends Thread implements ServerConstants {

    MulticastSocket socket;
    IServer server;
    ArrayList<ServerInfo> servidores;
    ServerInfo myServerInfo;
    InetAddress multicastGroupAddr;

    public MulticastListenerThread(MulticastSocket socket, IServer server, ArrayList<ServerInfo> servidores, ServerInfo myServerInfo) {
        this.socket = socket;
        this.server = server;
        this.servidores = servidores;
        this.myServerInfo = myServerInfo;
    }

    @Override
    public void run() {
        try {
            multicastGroupAddr = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(multicastGroupAddr);
        } catch (IOException e) {
            System.out.println("Não consegui juntar-me ao grupo de multicaste");
        }

        try{
            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                socket.receive(datagramPacket);

                String json = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("Recebi uma mensagem Multicast: " + json);

            }
        } catch (IOException e) {
            System.out.println("MulticastListenerThread - Tou a sair...");
        }
    }
}
