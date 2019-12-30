package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Playlist;
import Comum.Song;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
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
    ArrayList<Song> songs;
    ArrayList<Playlist> playlists;

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
        ObservableList<Node> children = vboxMyPlaylists.getChildren();
        for (int i = 1; i < children.size(); )
            children.remove(i);
        vboxMySongs.getChildren().clear();

        lblNomeUtilizador.setText(clientController.getUtilizador().getName());

        songs = clientController.getMyMusics();
        for (Song song: songs) {
            Button btn = new Button(song.getNome());
            btn.setAlignment(Pos.CENTER);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(evt->{
                try {
                    load("FXML/Musica.fxml", song, playlists, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            vboxMySongs.getChildren().add(btn);
        }

        playlists = clientController.getPlaylists();
        for (Playlist playlist: playlists) {
            Button btn = new Button(playlist.getNome());
            btn.setAlignment(Pos.CENTER);
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(evt->{
                try {
                    load("FXML/Playlist.fxml", null, null, playlist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            vboxMyPlaylists.getChildren().add(btn);
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

    public void handleNewPlaylist(ActionEvent actionEvent) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("New Playlist");
        textInputDialog.setHeaderText("Nome da playlist:");
        textInputDialog.showAndWait().ifPresent(s->{
            try {
                clientController.newPlaylist(s);
            } catch (InvalidPlaylistNameException e) {
                showAlert("Criar Playlist", e.getMessage());
            }
        });
    }
}
