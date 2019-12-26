package Cliente.JavaFX;

import Comum.Exceptions.InvalidSongDescriptionException;
import Comum.Song;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class NewSongController extends SceneController {


    public TextField txtNome;
    public TextField txtAlbum;
    public TextField txtAno;
    public TextField txtDuracao;
    public TextField txtGenero;
    public Label lblFicheiro;

    private File ficheiro;

    public void handleEscolherFicheiro(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            if(!selectedFile.canRead())
                return;
            ficheiro = selectedFile;
            lblFicheiro.setText(selectedFile.getName());
        }

    }

    public void handleCancelar(ActionEvent actionEvent) {
        try {
            load("FXML/Main.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSubmit(ActionEvent actionEvent) {
        if(clientController == null) return;
        if(ficheiro == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Upload Ficheiro", "Deve escolher um ficheiro");
            return;
        }

        try {
            clientController.uploadFile(new Song(txtNome.getText(), txtAlbum.getText(), Integer.parseInt(txtAno.getText()), Integer.parseInt(txtDuracao.getText()), txtGenero.getText(), ficheiro.getAbsolutePath()));
            load("FXML/Main.fxml");
        } catch (IOException | InvalidSongDescriptionException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Upload Ficheiro", e.getMessage());
        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Erro", "Upload Ficheiro", "Os campos Ano e Duração devem ser numeros");
        } catch (NullPointerException e){
            showAlert(Alert.AlertType.ERROR, "Erro", "Upload Ficheiro", "Os campos devem estar todos preenchidos");
        }
    }
}
