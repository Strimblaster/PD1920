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
    ArrayList<Song> musicasDownload;
    ArrayList<Song> songs;

    public Label lblNomePlaylist;
    public VBox vBox;
    public HBox hBox;
    public Button btnEdit;
    public Button btnPlayDownload;
    public Utilizador utilizador;

    @Override
    public void setClientController(ClientController controllerClient) {
        super.setClientController(controllerClient);

        musicasDownload = new ArrayList<>();
        for(Song s : playlist.getMusicas()){
            String[] list = musicDirectory.list((dir, name) -> name.equals(s.getFilename()));
            assert list != null;
            if(list.length == 0) {
                musicasDownload.add(s);
            }
        }

        if(musicasDownload.size() > 0) {
            btnPlayDownload.setText("Download");
            btnPlayDownload.setOnAction(this::handleDownload);
        }
        else {
            btnPlayDownload.setText("Play");
            btnPlayDownload.setOnAction(this::handlePlay);
        }

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
        if(playlist == null) return;
        if(playlist.getMusicas().size() == 0) return;
        try {
            load("FXML/MediaPlayer.fxml", null, null, playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDownload(ActionEvent actionEvent) {
        try {
            for(Song song : musicasDownload){
                clientController.downloadFile(song);
            }
        } catch (AlreadyDownloadingException e) {
            showAlert("Download", e.getMessage());
        }
    }
}
