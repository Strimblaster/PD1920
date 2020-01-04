package DS;

import Comum.RMIAppInterface;
import Comum.RMIDSInterface;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class RMIService extends java.rmi.server.UnicastRemoteObject implements RMIDSInterface {

    private final DS ds;
    HashMap<Integer, RMIAppInterface> listeners = new HashMap<>();
    int listenerCount = 0;

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

    @Override
    public int addListener(RMIAppInterface appInterface) throws RemoteException {
        listeners.put(++listenerCount, appInterface);
        return listenerCount;
    }

    @Override
    public void removeListener(int id) throws RemoteException {
        listeners.remove(id);
    }

    public void notifyListeners(InetAddress ip, int port) {
        for (RMIAppInterface appInterface: listeners.values()){
            try {
                appInterface.notifyClientRequest(ip, port);
            } catch (RemoteException e) {
                System.out.println("Não consegui notificar um cliente");
            }
        }
    }

    public void notifyListeners(ServerInfo serverInfo) {
        for (RMIAppInterface appInterface: listeners.values()){
            try {
                appInterface.notifyNewServer(serverInfo);
            } catch (RemoteException e) {
                System.out.println("Não consegui notificar um cliente");
            }
        }
    }
}
