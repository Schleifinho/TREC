package Controller;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class RateController
{
    @FXML private Slider stars;
    @FXML private TextArea text;

    @FXML private void rate()
    {
        if(Main.loggedIn && !text.getText().isEmpty() && text.getText().length() < 256)
        {
            MongoClient mongoClient = MongoClients.create();
            try
            {
                MongoDatabase db = mongoClient.getDatabase("OAD");
                MongoCollection<Document> collection = db.getCollection("Rating");

                boolean valid = checkIfAlreadyRated(collection);

                if(valid)
                {
                    Document rating = new Document();
                    rating.put("HotelID", HotelPageController.getHotelID());
                    rating.put("WriterID", Main.loggedInPerson.getId());
                    rating.put("Name", Main.loggedInPerson.getName() + " " + Main.loggedInPerson.getSurName());
                    rating.put("Stars", stars.getValue());
                    rating.put("Text", text.getText());

                    collection.insertOne(rating);

                    openStage();
                    Stage stage = (Stage) text.getScene().getWindow();
                    stage.close();
                }
                else
                {
                    System.out.print("Already rated!\n");
                }

            }
            catch(Exception e){System.out.print(e + "\n");}
            finally
            {
                mongoClient.close();
            }
        }
        else
        {
            if(text.getText().isEmpty())
                System.out.print("Please fill in some text!\n");
            else if(text.getText().length() > 256)
                System.out.print("Max Space 256!\n");
            else
                System.out.print("Login first\n");
        }
    }
    private void openStage()
    {
        try
        {
            URL url = new File("src/main/java/FXML/HotelPageFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            HomePageController.open_stage.setScene(new Scene(root));
            HomePageController.open_stage.setTitle("Hotel Page");
            HomePageController.open_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean checkIfAlreadyRated(MongoCollection<Document> collection)
    {
        BasicDBObject criteria = new BasicDBObject();
        criteria.append("HotelID", HotelPageController.getHotelID());
        criteria.append("WriterID",  Main.loggedInPerson.getId());
        Document rating = collection.find(criteria).first();

        if (rating == null)
            return true;
        else
            return false;
    }
}
