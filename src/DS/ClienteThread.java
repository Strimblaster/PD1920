package DS;

import Comum.Constants;

import java.io.*;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ClienteThread extends Thread implements Constants {

    DS ds;
    DatagramPacket datagramPacket;

    public ClienteThread(DS ds) {
        this.ds = ds;
    }

    public void run(){

            while(true){
                try {
                    datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                    ds.clienteDatagramSocket.receive(datagramPacket);
                    System.out.println("[INFO] [ThreadCliente] - Novo pedido!");

                    int nextServer = ds.getProximoServidor();
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout);
                    if(ds.getTotalServidores() > 0) {
                        System.out.println("[INFO] [ThreadCliente] - Cliente " + datagramPacket.getAddress().toString() + datagramPacket.getPort() + " atribuido o Servidor " + nextServer );
                        out.writeObject(ds.servidoresTCP.get(nextServer));
                    }else{
                        out.writeObject(null);
                    }
                    out.flush();
                    out.close();

                    byte[] resposta = bout.toByteArray();
                    datagramPacket.setData(resposta);
                    datagramPacket.setLength(resposta.length);
                    ds.clienteDatagramSocket.send(datagramPacket);

                    ds.incrementaProximoServidor();

                } catch (SocketException e) {
                    return;
                } catch (IOException e) {
                    System.out.println("[Erro] [ThreadCliente] - Erro: " + e.getMessage());
                }
            }
    }
}
