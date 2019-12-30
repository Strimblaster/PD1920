package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Exceptions.InvalidPlaylistNameException;
import Comum.Playlist;
import Comum.Song;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditPlaylistController extends SceneController {

    public TextField nome;
    public Playlist playlist;
    public VBox vBox;
    public ArrayList<Song> songs;
    public ArrayList<CheckBox> checkBoxes;

    //"Inicializer"
    @Override
    public void setClientController(ClientController clientController) {
        super.setClientController(clientController);
        nome.setText(playlist.getNome());
        songs = playlist.getMusicas();
        checkBoxes = new ArrayList<>();

        for (Song song : songs) {
            Label nomeSong = new Label(""+song.getNome());
            CheckBox checkBox = new CheckBox();
            checkBox.setId(""+song.getId());
            checkBoxes.add(checkBox);

            HBox hBox = new HBox(nomeSong, checkBox);
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
    }


    public void handleBtnGuardar(ActionEvent actionEvent) {

        try {
            if(nome.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Nome da playlist", "Deve escolher um nome para a musica");

            playlist.setNome(nome.getText());

            for(CheckBox checkBox : checkBoxes){
                if(checkBox.isSelected())
                    playlist.getMusicas().remove(checkBox);
            }

            if(!clientController.editPlaylist(playlist))
                showAlert(Alert.AlertType.ERROR, "Erro", "Nome da playlist", "Já existe playlist com este nome");

            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Playlist.fxml", null, null, playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }
}
