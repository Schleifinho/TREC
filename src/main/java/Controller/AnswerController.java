package Controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;


public class AnswerController
{
    private static ObjectId complainID;

    @FXML private Label text_subject;
    @FXML private Label text_complain;
    @FXML private TextArea text_answer;

    public static void initData(ObjectId id)
    {
        complainID = id;
    }

    @FXML private void sendAnswer()
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Help");
            System.out.print(complainID + "\n");
            Document complain = collection.find(eq("_id", complainID)).first();

            text_subject.setText(complain.get("Subject").toString());
            text_complain.setText(complain.get("Complain").toString());

            Bson filter = new Document("_id", complainID);
            Bson newValue = new Document("Answer", text_answer.getText());
            Bson updateOperationDocument = new Document("$set", newValue);
            collection.updateOne(filter, updateOperationDocument);
            openHelpCenter();

        }
        catch (Exception e){System.out.print(e + "\n");}
        finally
        {
            mongoClient.close();
        }
    }

    private void openHelpCenter()
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
