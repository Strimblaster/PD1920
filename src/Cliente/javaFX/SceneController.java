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
        Object obj = loader.load();
        if(obj instanceof  AnchorPane) root = (AnchorPane) obj;
        else root1 = (BorderPane) obj;

        if(!(loader.getController() instanceof SceneController)) return;

        SceneController controller = loader.getController();

        controller.stage = stage;
        controller.setClientController(clientController);

        if(root == null)
            stage.getScene().setRoot(root1);
        else
            stage.getScene().setRoot(root);

    }

}
