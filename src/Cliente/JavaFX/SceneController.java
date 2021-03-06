package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.Playlist;
import Comum.Song;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class SceneController {
    public ClientController clientController;
    public Stage stage;
    public File musicDirectory;

    public void setClientController(ClientController controllerClient) {
        this.clientController = controllerClient;
    }

//Codigo do load está mau mas funciona...
    public void load(String res) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(res));
        AnchorPane root = null;
        BorderPane root1 = null;
        VBox root2 = null;
        Object obj = loader.load();
        if(obj instanceof  AnchorPane) root = (AnchorPane) obj;
        if(obj instanceof VBox) root2 = (VBox) obj;
        else root1 = (BorderPane) obj;

        if(!(loader.getController() instanceof SceneController)) return;

        SceneController controller = loader.getController();

        controller.stage = stage;
        controller.musicDirectory = musicDirectory;
        clientController.setSceneController(controller);
        controller.setClientController(clientController);



        if(root1 != null)
            stage.getScene().setRoot(root1);
        else if(root2 != null)
            stage.getScene().setRoot(root2);
        else
            stage.getScene().setRoot(root);

    }

    public void load(String res, Song song, ArrayList<Playlist> playlists, Playlist playlist) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(res));
        AnchorPane root = null;
        BorderPane root1 = null;
        VBox root2 = null;
        Object obj = loader.load();
        if(obj instanceof  AnchorPane) root = (AnchorPane) obj;
        if(obj instanceof VBox) root2 = (VBox) obj;
        else root1 = (BorderPane) obj;

        if(!(loader.getController() instanceof SceneController)) return;

        SceneController controller = loader.getController();

        controller.stage = stage;
        controller.musicDirectory = musicDirectory;
        clientController.setSceneController(controller);
        if(controller instanceof MusicaController) {
            ((MusicaController) controller).setSong(song);
            ((MusicaController) controller).setPlaylists(playlists);
            ((MusicaController) controller).setUtilizador(clientController.getUtilizador());

        }
        else if(controller instanceof EditMusicController){
            ((EditMusicController) controller).setSong(song);
        }
        else if(controller instanceof PlaylistController){
            ((PlaylistController) controller).setPlaylist(playlist);
            ((PlaylistController) controller).setUtilizador(clientController.getUtilizador());
        }
        else if(controller instanceof EditPlaylistController){
            ((EditPlaylistController) controller).setPlaylist(playlist);
        }
        else if(controller instanceof MusicMediaPlayer){
            MusicMediaPlayer player = (MusicMediaPlayer) controller;
            if(playlist != null)
                player.setPlaylist(playlist);
            else
                player.setSong(song);
        }
        controller.setClientController(clientController);



        if(root1 != null)
            stage.getScene().setRoot(root1);
        else if(root2 != null)
            stage.getScene().setRoot(root2);
        else
            stage.getScene().setRoot(root);

    }

    public void showAlert(Alert.AlertType error, String titulo, String cabeçalho, String message) {
        Alert alert = new Alert(error);
        alert.setTitle(titulo);
        alert.setHeaderText(cabeçalho);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void showAlert(String cabeçalho, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(cabeçalho);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void setMusicDirectory(File musicDirectory) {
        this.musicDirectory = musicDirectory;
    }


}
