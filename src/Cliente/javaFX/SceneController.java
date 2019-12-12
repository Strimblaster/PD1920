package Cliente.javaFX;

import Cliente.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class SceneController {
    public ClientController clientController;
    public Stage stage;

    public void setClientController(ClientController controllerClient) {
        this.clientController = controllerClient;
    }


    public void load(String res) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(res));
        AnchorPane root = null;
        BorderPane root1 = null;
        try {
            root = loader.load();
        } catch (IOException e){
            root1 = loader.load();
        }

        Object obj =  loader.getController();
        if(!(obj instanceof SceneController)) return;

        SceneController controller = (SceneController) obj;

        controller.stage = stage;
        controller.setClientController(clientController);

        if(root == null)
            stage.getScene().setRoot(root1);
        else
            stage.getScene().setRoot(root);

    }

}
