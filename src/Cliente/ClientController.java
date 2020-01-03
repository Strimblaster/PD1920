package Cliente;

import Cliente.Interfaces.Listener;
import Cliente.JavaFX.SceneController;
import Comum.Exceptions.*;
import Comum.FilteredResult;
import Comum.Playlist;
import Comum.Song;
import Comum.Utilizador;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static Comum.Constants.CLIENT_DIR;

public class ClientController implements Listener {
    private ClientModel model;
    private SceneController sceneController;
    private File musicDirectory;
    private String clientRunningPath;
    private ArrayList<Song> songsBeingDownloaded;

    ClientController(String clientDir, String ip, int port) throws IOException, InvalidServerException {
        this.clientRunningPath = clientDir;
        this.model = new ClientModel(this, ip, port);
        songsBeingDownloaded = new ArrayList<>();
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

    @Override
    public void songDownloaded(byte[] file, File pathToSave, String nomeDaMusica) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                try {
                    //Guarda o ficheiro
                    FileOutputStream fileOutputStream = new FileOutputStream(pathToSave);
                    fileOutputStream.write(file);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    //Remover da lista de musicas a ser feito o download
                    songsBeingDownloaded.removeIf((song)-> song.getNome().equals(nomeDaMusica));

                    sceneController.showAlert(Alert.AlertType.CONFIRMATION, "Sucesso", "Download de musica", "Download da musica " + nomeDaMusica + " realizado com sucesso");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null) throw new InvalidUsernameException();
        if(username.equals("")) throw new InvalidUsernameException();
        if(password == null) throw new InvalidPasswordException();
        if(password.equals("")) throw new InvalidUsernameException();

        boolean sucess = model.login(username, password);
        if(sucess) {
            setFileDirectory();
            sceneController.setMusicDirectory(musicDirectory);
            model.setFileDir(musicDirectory);
        }
    }

    public void signUp(String username, String password) throws InvalidUsernameException, InvalidPasswordException, InvalidServerException {
        if(username == null) throw new InvalidUsernameException();
        if(username.equals("")) throw new InvalidUsernameException();
        if(password == null) throw new InvalidPasswordException();
        if(password.equals("")) throw new InvalidUsernameException();

        model.signUp(username, password);

    }

    public void uploadFile(Song musica) throws InvalidSongDescriptionException {
        model.uploadFile(musica);
    }

    public void downloadFile(Song musica) throws AlreadyDownloadingException {
        if(songsBeingDownloaded.contains(musica))
            throw new AlreadyDownloadingException(musica.getNome());
        songsBeingDownloaded.add(musica);
        model.downloadFile(musica);
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void setFileDirectory() {
        File clientFolder = new File(clientRunningPath + CLIENT_DIR);
        Utilizador utilizador = model.getUtilizador();
        File dir = new File(clientRunningPath + CLIENT_DIR + utilizador.getName());
        if(!clientFolder.exists()) {
            clientFolder.mkdirs();
            dir.mkdir();
            musicDirectory = dir;
        }
        else{
            if(!dir.exists())
                dir.mkdir();
            musicDirectory = dir;
        }
        System.out.println("Folder: " + musicDirectory.getAbsolutePath());
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
    public ArrayList<Playlist> getPlaylists(){
        return model.getPlaylists();
    }
    public boolean newPlaylist(String nome) throws InvalidPlaylistNameException {
        return model.newPlaylist(nome);
    }
    public boolean addSong(Playlist playlist, Song song) throws InvalidPlaylistNameException, InvalidSongDescriptionException {
        return model.addSong(playlist, song);
    }

    public boolean editFile(Song song) {
        return model.editFile(song);
    }

    public boolean editPlaylist(Playlist playlist) {
        return model.editPlaylist(playlist);
    }
}
