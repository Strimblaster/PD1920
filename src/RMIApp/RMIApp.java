package RMIApp;


import Comum.RMIAppInterface;
import Comum.RMIDSInterface;
import Comum.ServerInfo;
import DS.RMIService;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class RMIApp extends UnicastRemoteObject implements RMIAppInterface {
    public static final String RMIServiceName = "RegistryDS";
    public static final String RMIAppName = "DSListener";
    public int listenerID = -1;
    public static String registration = "rmi://localhost/"+ RMIAppName ;

    protected RMIApp() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        if(args.length!=1){
            System.out.println("RMIApp.jar ip_DS");
            return;
        }
        Scanner sc = new Scanner(System.in);
        int op;

        String URL = "rmi://" + args[0] + "/" + RMIServiceName;
        try{
            RMIDSInterface remoteInterface = (RMIDSInterface) Naming.lookup(URL);
            RMIApp rmiApp = new RMIApp();
            iniciarRMI(rmiApp);

            while(true) {
                try {
                    do {
                        System.out.println("--- RMI DS App --- ");
                        System.out.println("1- Lista de servers ativos");
                        System.out.println("2- Desligar server");
                        System.out.println("3- Registar-me como Listener");
                        System.out.println("4- Remover-me como Listener");
                        System.out.println("0- Sair\n");
                    } while ((op = sc.nextInt()) > 4 || op < 0);

                    switch (op) {
                        case 1:
                            ArrayList<ServerInfo> servers = remoteInterface.getActiveServers();
                            for (ServerInfo server : servers)
                                System.out.println(server);
                            break;
                        case 2:
                            System.out.println("ID do server a desligar (-1 para voltar)");
                            int id = sc.nextInt();
                            remoteInterface.turnOffServer(id);
                            break;
                        case 3:
                            rmiApp.listenerID = remoteInterface.addListener(rmiApp);
                            break;
                        case 4:
                            if (rmiApp.listenerID != -1)
                                remoteInterface.removeListener(rmiApp.listenerID);
                            break;
                        case 0:
                            Naming.unbind(registration);
                            UnicastRemoteObject.unexportObject(rmiApp, true);
                            return;
                    }
                } catch (InputMismatchException ignored) {
                }
            }
        } catch(ConnectException e){
            System.out.println("RMI do DS não está ativo");
        }

    }

    private static void iniciarRMI(RMIApp rmiApp) throws MalformedURLException, RemoteException {
        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }catch(RemoteException e){
            System.out.println("Atenção: RMIRegistry provavelmente já está a correr");
        }

        Naming.rebind( registration, rmiApp );
    }

    @Override
    public void notifyNewServer(ServerInfo server) {
        System.out.println("Notificação novo servidor: " + server);
    }

    @Override
    public void notifyClientRequest(InetAddress ipClient, int port) {
        System.out.println("Notificação : O Cliente " + ipClient.getHostName() + ":" + port + " pediu um servidor");
    }
}
