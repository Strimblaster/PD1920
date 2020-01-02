package Servidor.Threads;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.PedidoUploadFile;
import Comum.ServerInfo;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.ServerConstants;
import Servidor.Runnables.SignUpRunnable;
import Servidor.Runnables.UploadFileRunnable;
import Servidor.Utils.MulticastMessage;
import Servidor.Utils.MulticastMessageDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.SQLException;
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
        Gson gsonMulticastMsg = new GsonBuilder().registerTypeAdapter(MulticastMessage.class, new MulticastMessageDeserializer()).create();
        try {
            multicastGroupAddr = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(multicastGroupAddr);
        } catch (IOException e) {
            System.out.println("Não consegui juntar-me ao grupo de multicast");
        }

        try{
            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                socket.receive(datagramPacket);

                String json = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                MulticastMessage message;
                try{
                    message = gsonMulticastMsg.fromJson(json, MulticastMessage.class);
                } catch (JsonParseException e){
                    System.out.println("Não consegui deserializar a messagem");
                    continue;
                }
                ServerInfo receiver = message.getReceiver();
                Pedido pedido = message.getPedido();

                //Verifica se fui eu que mandei a mensagem
                if(message.getSender().getId() == myServerInfo.getId()) continue;

                //Verifica se é para mim
                if(receiver != null)
                    if(receiver.getId() != myServerInfo.getId())
                        continue;

                try {
                    if (pedido instanceof PedidoSignUp)
                        server.insertUser(pedido.getUtilizador());
                }catch (SQLException ignored){

                }

            }
        } catch (IOException e) {
            System.out.println("MulticastListenerThread - Tou a sair...");
        }
    }
}
