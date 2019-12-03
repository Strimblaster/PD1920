package DS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerThread extends Thread {

    DS ds;
    DatagramPacket datagramPacket;

    public ServerThread(DS ds) {
        this.ds = ds;
    }

    public void run(){
        while(true){
            datagramPacket = new DatagramPacket(new byte[ds.TAM_BYTE_ARRAY], ds.TAM_BYTE_ARRAY);
            try {
                ds.servidorDatagramSocket.receive(datagramPacket);
                ds.servidores.put(datagramPacket.getAddress(), datagramPacket.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(ds.servidorDatagramSocket != null)
                    ds.servidorDatagramSocket.close();
            }
        }
    }
}
