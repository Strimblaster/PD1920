package Cliente.javaFX;

import Cliente.ClientController;
import Comum.Song;
import Comum.Utilizador;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController extends SceneController {


    public Button btnUpload;
    public Button btnSearch;
    public Button btnVoltar;
    public VBox vboxMyPlaylists;
    public VBox vboxMySongs;
    public Label lblNomeUtilizador;

    public void handleBtnUpload(ActionEvent actionEvent) {
        try {
            load("NewSong.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //"Inicializer"
    @Override
    public void setClientController(ClientController clientController) {
        super.setClientController(clientController);

        vboxMyPlaylists.getChildren().clear();
        vboxMySongs.getChildren().clear();

        lblNomeUtilizador.setText(clientController.getUtilizador().getName());

        ArrayList<Song> songs = clientController.getMyMusics();
        for (Song song: songs) {
            Button btn = new Button(song.getNome());
            btn.setAlignment(Pos.CENTER);
            btn.setMaxWidth(Double.MAX_VALUE);
            vboxMySongs.getChildren().add(btn);
        }

    }

    public void handleBtnSearch(ActionEvent actionEvent) {
        try {
            load("Search.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
