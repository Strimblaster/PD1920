package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Exceptions.AlreadyDownloadingException;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlaylistController extends SceneController {

    Playlist playlist;
    ArrayList<Song> songs;

    public Label lblNomePlaylist;
    public VBox vBox;
    public HBox hBox;
    public Button btnEdit;
    public Utilizador utilizador;

    @Override
    public void setClientController(ClientController controllerClient) {
        super.setClientController(controllerClient);

        lblNomePlaylist.setText(playlist.getNome());
        songs = playlist.getMusicas();

        for (Song song : songs) {
            Label nomeMusica = new Label(""+song.getNome());

            HBox hBox = new HBox(nomeMusica);
            hBox.setSpacing(20);
            hBox.setAlignment(Pos.CENTER);
            hBox.setMinHeight(Region.USE_COMPUTED_SIZE);
            hBox.setMinWidth(Region.USE_COMPUTED_SIZE);
            hBox.setPrefHeight(50);
            hBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            hBox.setMaxHeight(Double.MAX_VALUE);
            hBox.setMaxWidth(Double.MAX_VALUE);

            vBox.getChildren().add(hBox);
        }

        if(!playlist.getCriador().getName().equals(utilizador.getName())){
            hBox.getChildren().remove(btnEdit);
        }
    }

    public void handleBtnVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnEditarPlaylist(ActionEvent actionEvent) {
        try {
            load("FXML/EditarPlaylist.fxml", null, null, playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public void handlePlay(ActionEvent actionEvent) {
        try {
            load("FXML/MediaPlayer.fxml", null, null, playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
