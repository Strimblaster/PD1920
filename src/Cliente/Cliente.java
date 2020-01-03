package Cliente;

import Cliente.JavaFX.MenuController;
import Comum.Exceptions.InvalidServerException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static javafx.application.Platform.exit;

public class Cliente extends javafx.application.Application {
    private static ClientController clientController ;

    public static void main(String[] args) throws IOException {
        try {
            if(args.length == 0)
                clientController = new ClientController(System.getProperty("user.dir"), null, -1);
            else if (args.length == 2)
                clientController = new ClientController(System.getProperty("user.dir"), args[0], Integer.parseInt(args[1]));
            else
                System.out.println("cliente.jar ip port");

        } catch (InvalidServerException | SocketTimeoutException | NumberFormatException e){
            System.out.println(e.getMessage());
            exit();
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JavaFX/FXML/Inicio.fxml"));
            BorderPane root =  loader.load();

            ((MenuController)loader.getController()).setClientController(clientController);
            ((MenuController)loader.getController()).stage = stage;



            Scene scene = new Scene(root);
            stage.setTitle("Streaming Service");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
    }

}
