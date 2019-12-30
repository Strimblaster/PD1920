package Cliente.JavaFX;

import Comum.Exceptions.InvalidPasswordException;
import Comum.Exceptions.InvalidServerException;
import Comum.Exceptions.InvalidUsernameException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
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
            load("FXML/Main.fxml");
        } catch (InvalidUsernameException | InvalidPasswordException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Login", e.getMessage());
        }
    }

    public void handleBtnSignUp(ActionEvent actionEvent) {
        if(clientController == null) return;

        try {
            clientController.signUp(txtUsername.getText(), txtPassword.getText());
            load("FXML/Inicio.fxml");
        } catch (InvalidUsernameException | InvalidPasswordException | IOException | InvalidServerException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Sign Up", e.getMessage());
        }
    }

    public void handleBtnGoToLogin(ActionEvent actionEvent) {
        try {
            load("FXML/Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnGoToSignUp(ActionEvent actionEvent) {
        try {
            load("FXML/SignUp.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlebtnGoToInicio(ActionEvent actionEvent) {
        try {
            load("FXML/Inicio.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
