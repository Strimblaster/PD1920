package DS;

import java.io.*;
import java.net.DatagramPacket;

public class ClienteThread extends Thread {

    DS ds;
    DatagramPacket datagramPacket;

    public ClienteThread(DS ds) {
        this.ds = ds;
    }

    public void run(){
        try {
            while(true){
                datagramPacket = new DatagramPacket(new byte[DS.TAM_BYTE_ARRAY], DS.TAM_BYTE_ARRAY);
                System.out.println("A espera de um cliente...");
                ds.clienteDatagramSocket.receive(datagramPacket);
                System.out.println("Mensagem recebida!");

                int nextServer = ds.getProximoServidor();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout);
                if(ds.getTotalServidores() > 0) {
                    out.writeObject(ds.servidores.get(nextServer));
                }else{
                    out.writeObject(null);
                }
                out.flush();
                out.close();

                byte[] resposta = bout.toByteArray();
                datagramPacket.setData(resposta);
                datagramPacket.setLength(resposta.length);
                ds.clienteDatagramSocket.send(datagramPacket);
                System.out.println("Mensagem enviada");

                ds.incrementaProximoServidor();

            }
        } catch (IOException e) {

        }
    }
}
