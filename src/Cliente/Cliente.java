package Cliente;

import Comum.Music;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Cliente extends Application {
    private ClientController clientController = new ClientController();
//    private Music music;
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
//            music = new Music("Caralho", "Justin Biver", "Pila", 1990,10,"Gay", "/tmp/");
            clientController.sendMessageDS();
//            Gson gson = new Gson();
//            String json = gson.toJson(music);
//            System.out.println(json);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("javaFX/login.fxml"));
            BorderPane root =  loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
    }

}
