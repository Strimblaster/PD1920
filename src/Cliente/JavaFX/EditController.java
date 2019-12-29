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
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditController extends SceneController {

    public TextField nome;
    public TextField album;
    public TextField genero;
    public TextField ano;
    public TextField duracao;
    public Song song;

    //"Inicializer"
    @Override
    public void setClientController(ClientController clientController) {
        super.setClientController(clientController);
        nome.setText(song.getNome());
        album.setText(song.getAlbum());
        genero.setText(song.getAlbum());
        ano.setText(song.getAno()+"");
        duracao.setText(song.getDuracao()+"");
    }


    public void handleBtnGuardar(ActionEvent actionEvent) {

        try {
            if(nome.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Nome da musica", "Deve escolher um nome para a musica");
            if(album.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Album da musica", "Deve escolher um album para a musica");
            if(genero.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Genero da musica", "Deve escolher um genero para a musica");
            if(ano.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Ano da musica", "Deve escolher um ano para a musica");
            if(duracao.getText().length()==0)
                showAlert(Alert.AlertType.ERROR, "Erro", "Duracao da musica", "Deve escolher uma duracao para a musica");


            song.setNome(nome.getText());
            song.setAlbum(album.getText());
            song.setGenero(genero.getText());
            song.setAno(Integer.parseInt(ano.getText()));
            song.setDuracao(Integer.parseInt(duracao.getText()));

            if(!clientController.editFile(song))
                showAlert(Alert.AlertType.ERROR, "Erro", "Nome da musica", "Já existe musica com este nome");

            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnVoltar(ActionEvent actionEvent) {
        try {
            load("FXML/Musica.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
