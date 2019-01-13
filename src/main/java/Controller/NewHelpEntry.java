package Controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.net.URL;

public class NewHelpEntry
{
    @FXML
    private TextField subject;
    @FXML private TextArea complains;

    ObjectId writerID = Main.loggedInPerson.getId();

    @FXML
    private void sendHelp()
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Help");
            String sub = "";
            String comp = "";

            if(subject.getText() != null && complains.getText() != null)
            {
                sub = subject.getText();
                comp = complains.getText();

                Document complain = new Document();
                complain.put("WriterID", writerID);
                complain.put("Subject", sub);
                complain.put("Complain", comp);

                collection.insertOne(complain);

                openStage();
            }

        }
        catch (Exception e){System.out.print(e +"\n");}
        finally
        {
            mongoClient.close();
        }
    }

    private void openStage()
    {
        try
        {
            URL url = new File("src/main/java/FXML/HelpCenterFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            HomePageController.open_stage.setScene(new Scene(root));
            HomePageController.open_stage.setTitle("HelpCenter");
            HomePageController.open_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
