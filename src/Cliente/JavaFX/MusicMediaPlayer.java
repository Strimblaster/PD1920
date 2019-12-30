package Cliente.JavaFX;

import Comum.Playlist;
import Comum.Song;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;


public class MusicMediaPlayer extends SceneController {

    public Slider volumeSlider;
    public Label lblTitulo;
    public VBox listaMusicas;
    public Button btnPlayPause;
    public Button btnAvancar;
    Media currentSong;
    MediaPlayer mediaPlayer;
    Playlist playlist;
    Song song;
    int current;
    boolean playing = false;

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        listaMusicas.getChildren().clear();
        lblTitulo.setText(playlist.getNome());

        for (Song song: playlist.getMusicas()) {
            Label label = new Label(song.getNome());
            label.setFont(new Font(16));
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            listaMusicas.getChildren().add(label);
        }
        btnPlayPause.setText("Play");
        current = 0;
        currentSong = new Media(new File(musicDirectory.getAbsolutePath() + File.separator + playlist.getMusicas().get(current).getFilename()).toURI().toString());
        initializeMediaPlayer();
    }

    public void setSong(Song song) {
        this.song = song;
        listaMusicas.getChildren().clear();
        lblTitulo.setText(song.getNome());
        btnPlayPause.setText("Play");
        btnAvancar.setDisable(true);
        currentSong = new Media(new File(musicDirectory.getAbsolutePath() + File.separator + song.getFilename()).toURI().toString());
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer(currentSong);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(t1.doubleValue()/100);
            }
        });
        volumeSlider.setValue(50);
        mediaPlayer.setVolume(0.5);
    }

    public void handleVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePlayPause(ActionEvent actionEvent) {
        if(playing) {
            mediaPlayer.pause();
            playing = false;
            btnPlayPause.setText("Play");
        }else {
            mediaPlayer.play();
            playing = true;
            btnPlayPause.setText("Pause");
        }
    }

    public void handleAvancarMusica(ActionEvent actionEvent) {
        current++;
        mediaPlayer.pause();
        currentSong = new Media(new File(musicDirectory.getAbsolutePath() + File.separator + playlist.getMusicas().get(current).getFilename()).toURI().toString());
        mediaPlayer = new MediaPlayer(currentSong);
        mediaPlayer.play();
        if(current+1 == playlist.getMusicas().size())
            btnAvancar.setDisable(true);
    }

    public void handleMudarVolume(DragEvent dragEvent) {
        mediaPlayer.setVolume(volumeSlider.getValue());
    }
}
