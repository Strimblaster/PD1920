package Cliente.javaFX;

import Cliente.ClientController;
import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends SceneController implements Initializable {


    public Button btnUpload;
    public Button btnSearch;
    public Button btnVoltar;
    public VBox playlists;
    public VBox muscias;


    public void handleBtnUpload(ActionEvent actionEvent) {
        try {
            load("NewSong.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    @Override
    public void setClientController(ClientController sceneController) {
        super.setClientController(sceneController);
        playlists.getChildren().clear();
        muscias.getChildren().clear();

    }
}
