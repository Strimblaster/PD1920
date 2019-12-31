package DS;

import Comum.Constants;
import Comum.ServerInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class PingThread extends Thread implements Constants {

    DS ds;


    public PingThread(DS ds) {
        this.ds = ds;
    }

    @Override
    public void run() {
        try {
            while (true) {

                Thread.sleep(PING_SLEEP_MS);

                ArrayList<ServerInfo> listaServidores = new ArrayList<>(ds.servidoresUDP);

                ds.servidorPingDatagramSocket.setSoTimeout(PING_TIMEOUT_MS);

                for (ServerInfo server: listaServidores) {
                    try{
                        String json;
                        synchronized (ds.servidoresUDP) {
                            json = new Gson().toJson(ds.servidoresUDP);
                        }
                        byte[] resp = json.getBytes();
                        DatagramPacket datagramPacket = new DatagramPacket(resp, resp.length, server.getIp(), server.getPort());

                        ds.servidorPingDatagramSocket.send(datagramPacket);

                        ds.servidorPingDatagramSocket.receive(datagramPacket);
                        server.incrementPingCount();

                    } catch (IOException e) {
                        synchronized (ds.servidoresUDP) {
                            ds.servidoresUDP.remove(server);
                        }
                        for(ServerInfo s: ds.servidoresTCP)
                            if(s.getId() == server.getId()){
                                ds.servidoresTCP.remove(s);
                                break;
                            }
                        System.out.println("[INFO] - [PingThread]: " + server.toString() + " não respondeu ao ping! Foi removido da lista de servidores.");
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
        System.out.println("[PingThread] - Estou a encerrar...");
    }
    }
}
