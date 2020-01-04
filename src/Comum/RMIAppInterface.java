package Comum;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIAppInterface extends Remote {

    void notifyNewServer(ServerInfo server) throws RemoteException;
    void notifyClientRequest(InetAddress ipClient, int port) throws RemoteException;

}
