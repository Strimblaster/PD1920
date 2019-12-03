package DS;

import Comum.ServerInfo;

import java.io.IOException;
import java.net.DatagramPacket;

public class ServerThread extends Thread {

    DS ds;
    DatagramPacket datagramPacket;
    int nServers = 0;

    public ServerThread(DS ds) {
        this.ds = ds;
    }

    public void run(){
        try {
            while(true){
                datagramPacket = new DatagramPacket(new byte[ds.TAM_BYTE_ARRAY], ds.TAM_BYTE_ARRAY);

                ds.servidorDatagramSocket.receive(datagramPacket);
                System.out.println("Entrou um servidor: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort());

                ds.servidores.add(new ServerInfo(datagramPacket.getAddress(), datagramPacket.getPort()));

                String id = nServers+"";
                byte[] resp = id.getBytes();
                datagramPacket.setData(resp);
                datagramPacket.setLength(resp.length);

                System.out.println("Vai ser o servidor " + id );
                ds.servidorDatagramSocket.send(datagramPacket);
                nServers++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(ds.servidorDatagramSocket != null)
                ds.servidorDatagramSocket.close();
        }
    }
}
