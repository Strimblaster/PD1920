package Cliente.Interfaces;

import Cliente.Cliente;
import Comum.Exceptions.*;
import Comum.*;

import java.io.IOException;

public interface IComunicacaoCliente extends IComunicacao {

    ServerInfo getServerInfo() throws IOException, InvalidServerException;
}
