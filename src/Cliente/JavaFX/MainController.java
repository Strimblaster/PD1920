package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Song;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class MainController extends SceneController {


    public Button btnUpload;
    public Button btnSearch;
    public Button btnVoltar;
    public VBox vboxMyPlaylists;
    public VBox vboxMySongs;
    public Label lblNomeUtilizador;

    public void handleBtnUpload(ActionEvent actionEvent) {
        try {
            load("FXML/NewSong.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //"Inicializer"
    @Override
    public void setClientController(ClientController clientController) {
        super.setClientController(clientController);

        fillMySongsPlaylists();

    }

    private void fillMySongsPlaylists() {
        vboxMyPlaylists.getChildren().clear();
        vboxMySongs.getChildren().clear();

        lblNomeUtilizador.setText(clientController.getUtilizador().getName());

        ArrayList<Song> songs = clientController.getMyMusics();
        for (Song song: songs) {
            Button btn = new Button(song.getNome());
            btn.setAlignment(Pos.CENTER);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(evt->{
                try {
                    load("FXML/Musica.fxml", song);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            vboxMySongs.getChildren().add(btn);
        }
    }

    public void handleBtnSearch(ActionEvent actionEvent) {
        try {
            load("FXML/Search.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Inicio.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent actionEvent) {
        fillMySongsPlaylists();
    }
}
