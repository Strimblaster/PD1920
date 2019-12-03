package DS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteThread extends Thread {

    DS ds;
    DatagramPacket datagramPacket;

    public ClienteThread(DS ds) {
        this.ds = ds;
    }

    public void run(){
        while(true){
            datagramPacket = new DatagramPacket(new byte[ds.TAM_BYTE_ARRAY], ds.TAM_BYTE_ARRAY);
            try {
                ds.clienteDatagramSocket.receive(datagramPacket);

                String ServidorIP = ds.servidores.keySet().toArray()[ds.getProximoServidor()].toString();
                String ServidorPort = ds.servidores.get(ds.getProximoServidor()).toString();

                byte b[] = new byte[ds.TAM_BYTE_ARRAY];
                b = ServidorIP.getBytes();
                DatagramPacket pkt = new DatagramPacket(b, b.length, datagramPacket.getAddress(), datagramPacket.getPort());
                ds.clienteDatagramSocket.send(pkt);

                b = ServidorPort.getBytes();
                pkt = new DatagramPacket(b, b.length, datagramPacket.getAddress(), datagramPacket.getPort());
                ds.clienteDatagramSocket.send(pkt);

                ds.incrementaProximoServidor();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(ds.clienteDatagramSocket != null)
                    ds.clienteDatagramSocket.close();
            }
        }
    }
}
