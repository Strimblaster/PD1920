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
        try {
            while(true){
                datagramPacket = new DatagramPacket(new byte[ds.TAM_BYTE_ARRAY], ds.TAM_BYTE_ARRAY);

                ds.clienteDatagramSocket.receive(datagramPacket);

                if(ds.servidores.isEmpty()){
                    String resposta = "Impossivel ligar-se, nenhum servidor online";
                    byte b[] = resposta.getBytes();
                    datagramPacket.setData(b);
                    datagramPacket.setLength(b.length);

                    ds.clienteDatagramSocket.send(datagramPacket);
                    continue;
                }

                String ServidorIP = ds.servidores.keySet().toArray()[ds.getProximoServidor()].toString();
                String ServidorPort = ds.servidores.get(ds.getProximoServidor()).toString();

                byte b[] = ServidorIP.getBytes();

                datagramPacket.setData(b);
                datagramPacket.setLength(b.length);
                ds.clienteDatagramSocket.send(datagramPacket);

                b = ServidorPort.getBytes();
                datagramPacket.setData(b);
                datagramPacket.setLength(b.length);
                ds.clienteDatagramSocket.send(datagramPacket);

                ds.incrementaProximoServidor();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(ds.clienteDatagramSocket != null)
                ds.clienteDatagramSocket.close();
        }
    }
}
