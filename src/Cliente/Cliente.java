package Cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Cliente extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
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

    public static void main(String[] args) {
        launch(args);
    }
}
