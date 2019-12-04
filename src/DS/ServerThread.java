package DS;

import Comum.Constants;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ServerThread extends Thread implements Constants {

    DS ds;
    DatagramPacket datagramPacket;
    int nServers = 0;

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
                System.out.println("[INFO] - [ThreadServer]: Pedido de servidor de: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort());


                //Converte a porta TCP enviada pelo servidor e adiciona à lista de servidores
                byte[] data = datagramPacket.getData();
                int porta = Integer.parseInt(new String(data, 0, datagramPacket.getLength()));

                ServerInfo serverInfoTCP = new ServerInfo(datagramPacket.getAddress(), porta);
                ds.servidoresTCP.add(serverInfoTCP);


                //Adiciona à lista de servidores o IP e porta UDP
                ds.servidoresUDP.add(new ServerInfo(datagramPacket.getAddress(), datagramPacket.getPort()));


                //Envia de volta o ID que o servidor vai ser
                String id = nServers + "";
                byte[] resp = id.getBytes();
                datagramPacket.setData(resp);
                datagramPacket.setLength(resp.length);

                System.out.println("[INFO] - [ThreadServer] O servidor " + serverInfoTCP.getIp().toString() + " está disponivel na porta " + serverInfoTCP.getPort());
                ds.servidorDatagramSocket.send(datagramPacket);
                nServers++;

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
