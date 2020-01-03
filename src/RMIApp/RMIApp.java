package RMIApp;


import Comum.RMIInterface;
import Comum.ServerInfo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class RMIApp {
    public static final String RMIServiceName = "RegistryDS";

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        if(args.length!=1){
            System.out.println("RMIApp.jar ip_DS");
            return;
        }
        Scanner sc = new Scanner(System.in);
        int op;

        String URL = "rmi://" + args[0] + "/" + RMIServiceName;
        RMIInterface remoteInterface = (RMIInterface) Naming.lookup(URL);

        while(true) {
            try {
                do {
                    System.out.println("--- RMI DS App --- ");
                    System.out.println("1- Lista de servers ativos");
                    System.out.println("2- Desligar server");
                    System.out.println("3- ");
                    System.out.println("0- Sair\n");
                } while ((op = sc.nextInt()) > 3 || op < 0);

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
                        break;
                    case 0:
                        return;
                }
            }catch (InputMismatchException ignored){}
        }

    }
}
