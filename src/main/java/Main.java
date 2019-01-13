import Objects.Person;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application{

    static Stage active_stage = new Stage();
    static Person loggedInPerson = new Person();
    static boolean loggedIn = false;

    @Override
    public void start(Stage stage) throws Exception
    {

        URL url = new File("src/main/java/FXML/HomePageFXML.fxml").toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);

        active_stage.setTitle("Welcome to T-REC");
        active_stage.setScene(scene);
        active_stage.setMaximized(true);
        active_stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
