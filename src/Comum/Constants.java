package Comum;

import java.io.File;

public interface Constants {
    String IP_DS = "localhost";
    int SERVER_PORT_DS = 5000;
    int CLIENT_PORT_DS = 5001;
    int SERVER_PORT_DS_PING = 5002;
    int PING_SLEEP_MS = 3000;
    int PING_TIMEOUT_MS = 1000;

    int PKT_SIZE = 8000;
    int TIMEOUT_5s = 5000;
    int TIMEOUT_2s = 2000;

    String CLIENT_DIR = File.separator + "temp" + File.separator +"clientes"+ File.separator;
    String SERVER_DIR = File.separator + "temp" + File.separator +"servers"+ File.separator;

}
