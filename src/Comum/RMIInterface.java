package Comum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIInterface extends Remote {

    ArrayList<ServerInfo> getActiveServers() throws RemoteException;
    void turnOffServer(int idServer) throws RemoteException;
}
