package Servidor.Interfaces;

import Comum.IComunicacao;
import Servidor.Servidor;

import java.io.IOException;

public interface IComunicacaoServer extends IComunicacao {

    void setID(int id);
}
