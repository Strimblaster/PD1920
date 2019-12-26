package Cliente;

import Cliente.Interfaces.IEvent;
import Cliente.javaFX.SceneController;
import Comum.Exceptions.*;
import Comum.FilteredResult;
import Comum.Pedidos.Resposta;
import Comum.Song;
import Comum.Utilizador;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Comum.Constants.CLIENT_DIR;

public class ClientController implements IEvent {
    private ClientModel model;
    private SceneController sceneController;
    private File musicDirectory;

    ClientController(String clientDir) throws IOException, InvalidServerException {
        super();
        setFileDirectory(clientDir);
        this.model = new ClientModel(this, musicDirectory);
        System.out.println(model.getServer());
    }

    @Override
    public void songUploaded(String nome) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                sceneController.showAlert(Alert.AlertType.CONFIRMATION, "Sucesso", "Upload de musica", "Upload da musica " + nome + " realizado com sucesso");
            }
        });

    }

    public void login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null) throw new InvalidUsernameException();
        if(username.equals("")) throw new InvalidUsernameException();
        if(password == null) throw new InvalidPasswordException();
        if(password.equals("")) throw new InvalidUsernameException();

        Resposta resposta = model.login(username, password);


    }

    public void signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null) throw new InvalidUsernameException();
        if(username.equals("")) throw new InvalidUsernameException();
        if(password == null) throw new InvalidPasswordException();
        if(password.equals("")) throw new InvalidUsernameException();


        Resposta resposta = model.signUp(username, password);

    }

    public void uploadFile(Song musica) throws InvalidSongDescriptionException {
        model.uploadFile(musica);
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setFileDirectory(String path) {
        File temp = new File(path + CLIENT_DIR);
        if(!temp.exists()) {
            temp.mkdirs();
            File dir = new File(path + CLIENT_DIR + "1");
            dir.mkdir();
            musicDirectory = dir;
        }
        else{
            File dir;

            for (int i = 1; (dir = new File(path + CLIENT_DIR + i)).exists(); i++);

            dir.mkdir();
            musicDirectory = dir;
        }
        musicDirectory.deleteOnExit();
        System.out.println(musicDirectory.getAbsolutePath());
    }

    public File getMusicDirectory() {
        return musicDirectory;
    }

    public Utilizador getUtilizador(){
        return model.getUtilizador();
    }

    public ArrayList<Song> getMyMusics() {
        return model.getMyMusics();
    }

    public FilteredResult search(boolean songs, boolean playlists, String nome, String album, String genero, int ano, int duracao) {
        return model.search(songs, playlists, nome, album, genero, ano, duracao);
    }
}
