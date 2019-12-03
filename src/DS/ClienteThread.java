package DS;

import Comum.ServerInfo;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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


                int nextServer = ds.getProximoServidor();

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(ds.servidores.get(nextServer));
                oout.flush();
                oout.close();

                byte[] resposta = bout.toByteArray();
                datagramPacket.setData(resposta);
                datagramPacket.setLength(resposta.length);
                ds.clienteDatagramSocket.send(datagramPacket);


                ds.incrementaProximoServidor();

            }
        } catch (IOException e) {

        }
    }
}
