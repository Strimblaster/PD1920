package Cliente;

import Cliente.javaFX.MenuController;
import Comum.Exceptions.InvalidServerException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Platform.exit;

public class Cliente extends Application {
    private ClientController clientController ;

    public Cliente() throws IOException {
        try {
            clientController = new ClientController();
        } catch (InvalidServerException e){
            System.out.println(e.getMessage());
            exit();
        }
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("javaFX/Inicio.fxml"));
            BorderPane root =  loader.load();

            ((MenuController)loader.getController()).setClientController(clientController);

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
    }

}
