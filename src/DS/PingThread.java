package DS;

import Comum.Constants;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class PingThread extends Thread implements Constants {

    DS ds;
    DatagramPacket datagramPacket;


    public PingThread(DS ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        while (true) {
            try {

                String string = "Estas on?";
                byte[] resp = string.getBytes();

                ArrayList<ServerInfo> listaServidores = ds.servidoresUDP;

                ds.servidorPingDatagramSocket.setSoTimeout(1000);

                for (ServerInfo server: listaServidores) {
                    try{
                        datagramPacket = new DatagramPacket(resp, resp.length, server.getIp(), server.getPort());
                        ds.servidorPingDatagramSocket.send(datagramPacket);

                        ds.servidorPingDatagramSocket.receive(datagramPacket);

                    } catch (SocketTimeoutException e) {
                        ds.servidoresUDP.remove(server);
                    }
                }

            } catch (IOException e) {
                System.out.println("[Erro] [ServidorThread] - Erro: " + e.getMessage());
            }
        }
    }
}
