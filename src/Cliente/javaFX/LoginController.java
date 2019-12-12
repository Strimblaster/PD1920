package Cliente.javaFX;

import Cliente.ClientController;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private ClientController clientController;

    public Button btnLogin;
    public Button btnCancel;
    public TextField txtUsername;
    public PasswordField txtPassword;

    public void handleBtnLogin(ActionEvent actionEvent) {
        if(clientController == null) return;

        try {
            clientController.login(txtUsername.getText(), txtPassword.getText());
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            e.getMessage();
        }
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}
