package Cliente.JavaFX;

import Cliente.ClientController;
import Comum.FilteredResult;
import Comum.Song;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
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
    public HBox basePlaylistRow;
    public VBox playlistsList;
    public HBox baseSongRow;
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

                musicasList.getChildren().add(row);
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
