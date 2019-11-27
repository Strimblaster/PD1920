package Servidor;

import Comum.IComunicacao;
import java.net.*;
import java.util.ArrayList;

public class Comunicacao implements IComunicacao {
    ServerSocket s;
    ArrayList<Socket> clientes;

    public Comunicacao(ServerSocket s) {
        this.s = s;
    }
}
