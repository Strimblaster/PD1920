package Cliente;

import java.io.IOException;

class ClientController {
    private ClientModel model;

    ClientController() {
        super();
        this.model = new ClientModel();
    }

    void sendMessageDS() throws IOException {
        model.sendMessageDS();
    }

}
