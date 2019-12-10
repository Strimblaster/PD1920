package Cliente;

import Comum.Constants;
import Comum.Exceptions.InvalidServerException;
import Comum.ServerInfo;
import Comum.Utilizador;
import com.google.gson.Gson;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


class ClientModel extends Comunicacao implements Constants {

    private ServerInfo server;
    private Utilizador utilizador;

    public ClientModel() throws IOException, InvalidServerException {
        super();
        this.server = getServerInfo();
    }

}
