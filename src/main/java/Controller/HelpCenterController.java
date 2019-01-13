package Controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.fxml.FXML;
import org.bson.types.ObjectId;

public class HelpCenterController
{
    @FXML private TextField subject;
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

                Stage stage = (Stage) subject.getScene().getWindow();
                stage.close();
            }

        }
        catch (Exception e){System.out.print(e +"\n");}
        finally
        {
            mongoClient.close();
        }
    }
}
