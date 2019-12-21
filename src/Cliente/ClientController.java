package Cliente;

import Comum.Exceptions.*;
import Comum.Pedidos.Resposta;
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

        Resposta resposta = model.login(username, password);


    }

    public void signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null || username.equals("")) throw new InvalidUsernameException();
        if(password == null || password.equals("")) throw new InvalidPasswordException();

        Resposta resposta = model.signUp(username, password);
        System.out.println(resposta);


    }
}
