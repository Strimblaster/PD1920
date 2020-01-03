package DS;

import Comum.RMIInterface;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class RMIService extends java.rmi.server.UnicastRemoteObject implements RMIInterface {

    private final DS ds;

    public RMIService(DS ds) throws RemoteException {
        this.ds = ds;
    }

    @Override
    public ArrayList<ServerInfo> getActiveServers() {
        synchronized (ds.servidoresTCP){
            return ds.servidoresTCP;
        }
    }

    @Override
    public void turnOffServer(int idServer) {
        ServerInfo serverToShutdown = null;
        synchronized (ds.servidoresUDP){
            for (ServerInfo server: ds.servidoresUDP)
                if(server.getId() == idServer)
                    serverToShutdown = server;
        }

        byte[] bytes = "SAIR".getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverToShutdown.getIp(), serverToShutdown.getPort());
        try {
            ds.servidorPingDatagramSocket.send(packet);
        } catch (IOException ignored) {//Já não está ligado
        }
    }
}
