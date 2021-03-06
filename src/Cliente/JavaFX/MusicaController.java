package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Exceptions.AlreadyDownloadingException;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicaController extends SceneController {

    Song song;
    ArrayList<Playlist> playlists;

    public Label lblNomeMusica;
    public Label lblAutor;
    public Label lblAlbum;
    public Label lblGenero;
    public Label lblAno;
    public Label lblDuracao;
    public Button btnPlayDownload;
    public Button btnEdit;
    public Utilizador utilizador;
    public HBox hBox;

    @Override
    public void setClientController(ClientController controllerClient) {
        super.setClientController(controllerClient);

        lblNomeMusica.setText(song.getNome());
        lblAutor.setText(song.getAutor().getName());
        lblAlbum.setText(song.getAlbum());
        lblGenero.setText(song.getAlbum());
        lblAno.setText(song.getAno()+"");
        lblDuracao.setText(song.getDuracao()+"");

        if(song.getFilename() == null){
            btnPlayDownload.setText("Indisponivel");
            btnPlayDownload.setDisable(true);
            return;
        }

        //Verifica se o ficheiro existe na diretoria
        String[] list = musicDirectory.list((dir, name) -> name.equals(song.getFilename()));
        assert list != null;

        if(list.length != 0) {
            btnPlayDownload.setText("Play");
            btnPlayDownload.setOnAction(this::handleBtnPlay);
        }
        else {
            btnPlayDownload.setText("Download");
            btnPlayDownload.setOnAction(this::handleBtnDownload);
        }

        if(!song.getAutor().getName().equals(utilizador.getName())){
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

    public void handleBtnAdicionarPlaylist(ActionEvent actionEvent) {
        if(playlists == null){
            showAlert("Playlists", "Não tens playlists (refresh)");
            return;
        }
        ChoiceDialog<Playlist> choiceDialog = new ChoiceDialog<>(playlists.get(0), playlists);
        choiceDialog.setTitle("Adicionar à playlist");
        choiceDialog.setHeaderText("");
        choiceDialog.setContentText("Escolha a playlist:");
        choiceDialog.showAndWait().ifPresent( selected ->{
            try {
                clientController.addSong(selected, song);
            } catch (InvalidPlaylistNameException | InvalidSongDescriptionException e) {
                showAlert("Adicionar", e.getMessage());
            }
        });
    }

    public void handleBtnPlay(ActionEvent actionEvent) {
        try {
            load("FXML/MediaPlayer.fxml", song, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnDownload(ActionEvent actionEvent) {
        try {
            clientController.downloadFile(song);
        } catch (AlreadyDownloadingException e) {
            showAlert("Download", e.getMessage());
        }
    }

    public void handleBtnEditarMusica(ActionEvent actionEvent) {
        try {
            load("FXML/EditarMusica.fxml", song, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSong(Song song) {
        this.song = song;
    }
    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }
    public void setUtilizador(Utilizador utilizador){
        this.utilizador = utilizador;
    }
}
