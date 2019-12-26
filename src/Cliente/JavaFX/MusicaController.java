package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Song;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;

public class MusicaController extends SceneController {

    Song song;

    public Label lblNomeMusica;
    public Label lblAutor;
    public Label lblAlbum;
    public Label lblGenero;
    public Label lblAno;
    public Label lblDuracao;
    public Button btnPlayDownload;




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



    }

    public void handleBtnVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnAdicionarPlaylist(ActionEvent actionEvent) {
    }

    public void handleBtnPlay(ActionEvent actionEvent) {
        Media hit = new Media(new File(musicDirectory.getAbsolutePath() + File.separator + song.getFilename()).toURI().toString() );
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        MediaView mediaView = new MediaView(mediaPlayer);
        Scene scene = new Scene(new Group(mediaView));
        stage.setScene(scene);
        mediaPlayer.play();
    }

    public void handleBtnDownload(ActionEvent actionEvent) {

    }



    public void setSong(Song song) {
        this.song = song;
    }
}
