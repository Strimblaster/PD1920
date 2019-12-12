package Cliente.javaFX;

import Cliente.ClientController;
import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidUsernameException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MenuController extends SceneController {

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

    public void handleBtnGoToLogin(ActionEvent actionEvent) {
        try {
            load("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnGoToSignUp(ActionEvent actionEvent) {
        try {
            load("SignUp.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
