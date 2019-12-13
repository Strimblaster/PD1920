package Servidor.Interfaces;

import Comum.IComunicacao;
import Servidor.Servidor;

import java.io.IOException;

public interface IServer extends IComunicacao {

    void setID(int id);
}
