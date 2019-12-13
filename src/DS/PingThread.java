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
                Thread.sleep(PING_SLEEP_MS);
                String string = "Estas on?";
                byte[] resp = string.getBytes();

                ArrayList<ServerInfo> listaServidores = new ArrayList<>(ds.servidoresUDP);

                ds.servidorPingDatagramSocket.setSoTimeout(PING_TIMEOUT_MS);

                for (ServerInfo server: listaServidores) {
                    try{
                        datagramPacket = new DatagramPacket(resp, resp.length, server.getIp(), server.getPort());
                        ds.servidorPingDatagramSocket.send(datagramPacket);

                        ds.servidorPingDatagramSocket.receive(datagramPacket);

                    } catch (IOException e) {
                        ds.servidoresUDP.remove(server);
                        for(ServerInfo s: ds.servidoresTCP)
                            if(s.getId() == server.getId()){
                                ds.servidoresTCP.remove(s);
                                break;
                            }
                        System.out.println("[INFO] - [ThreadPings]: " + server.toString() + " não respondeu ao ping! Foi removido da lista de servidores.");
                    }
                }

            } catch (IOException | InterruptedException e) {
                System.out.println("[Erro] [ServidorThread] - Erro: " + e.getMessage());
            }
        }
    }
}
