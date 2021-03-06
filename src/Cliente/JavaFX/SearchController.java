package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.FilteredResult;
import Comum.Playlist;
import Comum.Song;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SearchController extends SceneController {


    public CheckBox filterSongs;
    public CheckBox filterPlaylists;
    public TextField filterAlbum;
    public TextField filterGenero;
    public TextField filterAno;
    public TextField filterNome;
    public TextField filterDuracao;
    public VBox playlistsList;
    public VBox musicasList;


    @Override
    public void setClientController(ClientController controllerClient) {
        super.setClientController(controllerClient);
        clear();

    }

    public void handleUpdate(ActionEvent actionEvent) {
        clear();
        int ano = -1, duracao = -1;
        try {
            ano = Integer.parseInt(filterAno.getText());
        }catch (NumberFormatException ignored){ }
        try {
            duracao = Integer.parseInt(filterDuracao.getText());
        }catch (NumberFormatException ignored){ }

        FilteredResult result = clientController.search(
                filterSongs.isSelected(),
                filterPlaylists.isSelected(),
                filterNome.getText(),
                filterAlbum.getText(),
                filterGenero.getText(),
                ano,
                duracao);

        try {
            for (Song song: result.songs) {

                HBox row = new FXMLLoader(getClass().getResource("FXML/SongRow.fxml")).load();
                ObservableList<Node> children = row.getChildren();
                ((Label)children.get(0)).setText(song.getNome());
                ((Label)children.get(1)).setText(song.getGenero());
                ((Label)children.get(2)).setText(song.getAlbum());
                ((Label)children.get(3)).setText(song.getAno()+"");
                ((Label)children.get(4)).setText(song.getDuracao()+"");

                row.setOnMouseClicked(mouseEvent -> {
                    try {
                        load("FXML/Musica.fxml", song, null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                musicasList.getChildren().add(row);
            }

            for (Playlist playlist: result.playlists) {

                HBox row = new FXMLLoader(getClass().getResource("FXML/PlaylistRow.fxml")).load();
                ObservableList<Node> children = row.getChildren();
                ((Label)children.get(0)).setText(playlist.getNome());
                ((Label)children.get(1)).setText(playlist.getCriador().getName());

                row.setOnMouseClicked(mouseEvent -> {
                    try {
                        load("FXML/Playlist.fxml", null, null, playlist);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                playlistsList.getChildren().add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void clear() {
        //Limpar a musicas e as playlists
        ObservableList<Node> playlistsChildren = playlistsList.getChildren();
        ObservableList<Node> musicasListChildren = musicasList.getChildren();
        for (int i = 2; i != playlistsChildren.size();)
            playlistsChildren.remove(playlistsChildren.get(i));
        for (int i = 2; i != musicasListChildren.size();)
            musicasListChildren.remove(musicasListChildren.get(i));
    }

    public void handleVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
