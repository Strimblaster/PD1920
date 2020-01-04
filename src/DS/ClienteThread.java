package DS;

import Comum.Constants;
import Comum.ServerInfo;
import com.google.gson.Gson;

import java.io.*;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ClienteThread extends Thread implements Constants {

    private DS ds;
    private DatagramPacket datagramPacket;

    public ClienteThread(DS ds) {
        this.ds = ds;
    }

    public void run(){

            while(true){
                try {
                    datagramPacket = new DatagramPacket(new byte[PKT_SIZE], PKT_SIZE);
                    ds.clienteDatagramSocket.receive(datagramPacket);
                    System.out.println("[INFO] - [ThreadCliente]: - Novo pedido!");

                    int nextServer = ds.getProximoServidor();

                    if(ds.getTotalServidores() > 0) {
                        ServerInfo serverInfo = ds.servidoresTCP.get(nextServer);
                        Gson gson = new Gson();
                        String jsonServerInfo = gson.toJson(serverInfo);
                        ds.notifyListeners(datagramPacket.getAddress(), datagramPacket.getPort());
                        System.out.println("[INFO] - [ThreadCliente]: - Cliente " + datagramPacket.getAddress().toString() + ":"+ datagramPacket.getPort() + " atribuido o Servidor " + nextServer );
                        byte[] jsonBytes = jsonServerInfo.getBytes();
                        datagramPacket.setData(jsonBytes);
                        datagramPacket.setLength(jsonBytes.length);
                    }else{
                        datagramPacket.setData("".getBytes());
                        datagramPacket.setLength("".getBytes().length);
                    }

                    ds.clienteDatagramSocket.send(datagramPacket);

                } catch (SocketException e) {
                    return;
                } catch (IOException e) {
                    System.out.println("[Erro] [ThreadCliente] - Erro: " + e.getMessage());
                }
            }
    }
}
