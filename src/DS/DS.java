package DS;

import Comum.Constants;
import Comum.ServerInfo;

import java.io.IOException;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class DS implements Constants {
    public final String RMIServiceName = "RegistryDS";
    private int proximoServidor;

    final ArrayList<ServerInfo> servidoresTCP;
    final ArrayList<ServerInfo> servidoresUDP;
    DatagramSocket servidorDatagramSocket;
    DatagramSocket servidorPingDatagramSocket;
    DatagramSocket clienteDatagramSocket;



    public DS() throws SocketException {
        servidorDatagramSocket = new DatagramSocket(SERVER_PORT_DS);
        clienteDatagramSocket = new DatagramSocket(CLIENT_PORT_DS);
        servidorPingDatagramSocket = new DatagramSocket(SERVER_PORT_DS_PING);
        proximoServidor = -1;
        servidoresTCP = new ArrayList<>();
        servidoresUDP = new ArrayList<>();
    }
    int getTotalServidores(){
        return servidoresTCP.size();
    }

    public int getProximoServidor(){
        servidoresTCP.sort(Comparator.comparingInt(ServerInfo::getId));
        proximoServidor++;
        if(this.proximoServidor >= servidoresTCP.size())
            proximoServidor = 0;
        return proximoServidor;
    }



    public static void main(String[] args) throws SocketException, InterruptedException, RemoteException, MalformedURLException {
        DS ds = new DS();
        Thread servidorThread = new ServerThread(ds);
        Thread clienteThread = new ClienteThread(ds);
        Thread pingServidores = new PingThread(ds);
        lancaRmi(ds);

        servidorThread.start();
        clienteThread.start();
        pingServidores.start();

        servidorThread.join();
        clienteThread.join();
        pingServidores.join();
    }

    private static void lancaRmi(DS ds) throws RemoteException, MalformedURLException {
        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }catch(RemoteException e){
            e.printStackTrace();
            return;
        }

        RMIService rmiService = new RMIService(ds);

        String registration = "rmi://localhost/"+ ds.RMIServiceName ;
        Naming.rebind( registration, rmiService );

    }

    public int getNextID() {
        //servidoresUDP.sort( (s1,s2) -> s1.getId() - s2.getId());
        //É a mesma coisa o intellij é que sugeriu
        servidoresUDP.sort(Comparator.comparingInt(ServerInfo::getId));
        int i;
        //Nada para ver aqui
        for (i = 0; i < servidoresUDP.size() && i == servidoresUDP.get(i).getId(); i++) ;

        return i;
    }
}
