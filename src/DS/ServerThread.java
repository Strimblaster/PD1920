package DS;

import Comum.Constants;
import Comum.ServerInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerThread extends Thread implements Constants {

    DS ds;
    DatagramPacket datagramPacket;

    public ServerThread(DS ds) {
        this.ds = ds;
    }

    @Override
    public void run(){

        while(true){
            try {
                datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);

                //Espera packet de um servidor
                ds.servidorDatagramSocket.receive(datagramPacket);



                //Converte a porta TCP enviada pelo servidor e adiciona à lista de servidores
                byte[] data = datagramPacket.getData();
                int porta = Integer.parseInt(new String(data, 0, datagramPacket.getLength()));
                int id;
                ServerInfo serverInfoTCP;
                id = ds.getNextID();
                serverInfoTCP = new ServerInfo(datagramPacket.getAddress(), porta, id);
                ds.servidoresTCP.add(serverInfoTCP);
                System.out.println("[INFO] - [ThreadServer]: Pedido de servidor de: " + serverInfoTCP.toString());


                //Adiciona à lista de servidores o IP e porta UDP
                synchronized (ds.servidoresUDP) {
                    ds.servidoresUDP.add(new ServerInfo(datagramPacket.getAddress(), datagramPacket.getPort(), id));
                }

                ds.notifyListeners(new ServerInfo(datagramPacket.getAddress(), datagramPacket.getPort(), id));

                //Envia de volta o ID que o servidor vai ser
                String strid = id + "";
                byte[] resp = strid.getBytes();
                datagramPacket.setData(resp);
                datagramPacket.setLength(resp.length);

                ds.servidorDatagramSocket.send(datagramPacket);
                System.out.println("[INFO] - [ThreadServer]: O servidor " + serverInfoTCP.getIp().toString() + " está disponivel na porta " + serverInfoTCP.getPort());

                //Envia Lista de servers
                byte[] listaServers = new Gson().toJson(ds.servidoresUDP).getBytes();
                datagramPacket.setData(listaServers);
                datagramPacket.setLength(listaServers.length);
                ds.servidorDatagramSocket.send(datagramPacket);

            } catch (SocketException e) {
                return;
            } catch (NumberFormatException e) {
                System.out.println("[INFO] - [ThreadServer]: Pedido de servidor com porta TCP invalida! " + e.getMessage());
            } catch (IOException e) {
                System.out.println("[Erro] [ThreadServer] - Erro: " + e.getMessage());
            }
        }
    }
}
