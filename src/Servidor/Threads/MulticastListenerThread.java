package Servidor.Threads;

import Comum.Pedidos.Pedido;
import Comum.Pedidos.PedidoNewPlaylist;
import Comum.Pedidos.PedidoSignUp;
import Comum.Pedidos.PedidoUploadFile;
import Comum.ServerInfo;
import Servidor.Interfaces.IServer;
import Servidor.Interfaces.ServerConstants;
import Servidor.Utils.MulticastConfirmationMessage;
import Servidor.Utils.MulticastMessage;
import Servidor.Utils.MulticastMessageDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;

public class MulticastListenerThread extends Thread implements ServerConstants {

    MulticastSocket socketReceive;
    DatagramSocket datagramSocket;
    IServer server;
    ArrayList<ServerInfo> servidores;
    ServerInfo myServerInfo;
    InetAddress multicastGroupAddr;
    InetAddress multicastGroupAddrConfirmation;


    public MulticastListenerThread(MulticastSocket socket, DatagramSocket datagramSocket, IServer server, ArrayList<ServerInfo> servidores, ServerInfo myServerInfo) {
        this.socketReceive = socket;
        this.server = server;
        this.servidores = servidores;
        this.myServerInfo = myServerInfo;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        Gson gsonMulticastMsg = new GsonBuilder().registerTypeAdapter(MulticastMessage.class, new MulticastMessageDeserializer()).create();
        try {
            multicastGroupAddr = InetAddress.getByName(MULTICAST_ADDR);
            multicastGroupAddrConfirmation = InetAddress.getByName(MULTICAST_ADDR_CONFIRMATION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Não consegui juntar-me ao grupo de multicast");
        }

        try{
            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                socketReceive.receive(datagramPacket);

                String json = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                MulticastMessage message;
                try{
                    message = gsonMulticastMsg.fromJson(json, MulticastMessage.class);
                } catch (JsonParseException e){
                    e.printStackTrace();
                    System.out.println("Não consegui deserializar a messagem");
                    continue;
                }

                //Verifica se a mensagem é para mim
                if(!checkMessage(message))
                    continue;

                Pedido pedido = message.getPedido();

                System.out.println("Recebi: " + json);

                try {
                    if (pedido instanceof PedidoSignUp)
                        server.insertUser(pedido.getUtilizador());
                    else if (pedido instanceof PedidoUploadFile) {
                        byte[] file = null;
                        if(message.getFile() != null) file = Base64.getDecoder().decode(message.getFile());
                        server.saveSongFile_Partial(((PedidoUploadFile) pedido).getMusica(), file);
                    }
                    else if (pedido instanceof PedidoNewPlaylist)
                        server.insertPlaylist(pedido.getUtilizador(), ((PedidoNewPlaylist) pedido).getNome());


                }catch (Exception ignored){

                }

                MulticastConfirmationMessage confirmationMessage = new MulticastConfirmationMessage(myServerInfo, message.getSender(), true);
                String toJson = new Gson().toJson(confirmationMessage);
                byte[] bytes = toJson.getBytes();
                System.out.println("Enviei confirmação: " + toJson);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, multicastGroupAddrConfirmation, MULTICAST_PORT_CONFIRMATION);
                datagramSocket.send(packet);
            }
        } catch (IOException e) {

            System.out.println("MulticastListenerThread - Tou a sair...");
        }
    }

    private boolean checkMessage(MulticastMessage message) {

        ServerInfo receiver = message.getReceiver();

        //Verifica se fui eu que mandei a mensagem
        if(message.getSender().getId() == myServerInfo.getId()) return false;

        //Verifica se é para mim
        if(receiver != null)
            if(receiver.getId() != myServerInfo.getId())
                return false;
        return true;
    }

}
