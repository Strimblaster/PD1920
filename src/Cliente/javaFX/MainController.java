package Cliente.javaFX;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends SceneController implements Initializable {


    public Button btnUpload;
    public Button btnSearch;
    public Button btnVoltar;

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
}
