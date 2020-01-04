package Comum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIDSInterface extends Remote {

    ArrayList<ServerInfo> getActiveServers() throws RemoteException;
    void turnOffServer(int idServer) throws RemoteException;
    int addListener(RMIAppInterface appInterface) throws RemoteException;
    void removeListener(int id) throws RemoteException;
}
