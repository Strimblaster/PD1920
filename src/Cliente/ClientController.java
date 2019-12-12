package Cliente;

import Comum.Exceptions.*;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class ClientController {
    private ClientModel model;

    ClientController() throws IOException, InvalidServerException {
        super();
        this.model = new ClientModel();
        System.out.println(model.getServer());
    }


    public void login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null || username.equals("")) throw new InvalidUsernameException();
        if(password == null || password.equals("")) throw new InvalidPasswordException();

        model.login(username, password);
    }
}
