package Controller;

import Objects.Person;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
        System.out.print(url +"\n");
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);

        active_stage.setTitle("Welcome to T-REC");
        active_stage.setScene(scene);
        active_stage.setMaximized(true);
        active_stage.show();
    }

    public static void main(String[] args)
    {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        launch(args);
    }
}
