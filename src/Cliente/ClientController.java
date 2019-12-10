package Cliente;

import Comum.Exceptions.InvalidServerException;

import java.io.IOException;

class ClientController {
    private ClientModel model;

    ClientController() throws IOException, InvalidServerException {
        super();
        this.model = new ClientModel();
        System.out.println(model.getServerInfo().toString());
    }


}
