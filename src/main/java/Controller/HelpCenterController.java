package Controller;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.fxml.FXML;
import org.bson.types.ObjectId;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static com.mongodb.client.model.Filters.eq;

public class HelpCenterController implements Initializable
{
    @FXML private ListView listView;

    public static final ObservableList data =
            FXCollections.observableArrayList();

    private boolean Admin = false;

    ObjectId writerID = Main.loggedInPerson.getId();
    ObjectId comlainID = new ObjectId();

    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Help");

        List<Document> documents = new ArrayList<Document>();

        if(Main.loggedInPerson.getRole().equals("Admin"))
        {
            documents = (List<Document>) collection.find().into(
                    new ArrayList<Document>());

            Admin = true;
        }
        else
        {
            documents = (List<Document>) collection.find(eq("WriterID" , writerID)).into(
                    new ArrayList<Document>());

            Admin = false;
        }

        fillListView(documents);
    }

    @FXML
    public void onListViewClick(MouseEvent arg)
    {
        if(arg.getClickCount() == 2 && Admin)
        {
            loadAnswerDialog((HBox)listView.getSelectionModel().getSelectedItem());
            System.out.print("Double clicked " + listView.getSelectionModel().getSelectedItems().get(0));
        }
    }

    private void loadAnswerDialog(HBox selectedItem)
    {
        ObjectId id = new ObjectId(selectedItem.getId());
        AnswerController.initData(id);
        try
        {
            URL url = new File("src/main/java/FXML/AnswerDialogFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            HomePageController.open_stage.setScene(new Scene(root));
            HomePageController.open_stage.setTitle("Answer");
            HomePageController.open_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void fillListView(List<Document> documents)
    {
        data.clear();
        for(Document document : documents)
        {
            data.addAll(listEntry(document));
            listView.setItems(data);
        }
    }

    public HBox listEntry(final Document document)
    {
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        hBox.setId(document.get("_id").toString());
        vBox.setMinWidth(570);
        comlainID = (ObjectId)document.get("_id");

        Label subject = new Label(document.get("Subject").toString());
        subject.setStyle("-fx-font-weight: bold;"+"-fx-font-size: 14");

        Label complain = new Label(document.get("Complain").toString());
        complain.setMaxWidth(250);
        complain.setWrapText(true);

        Label answer = new Label();
        try
        {
             answer.setText(document.get("Answer").toString());
        }
        catch (Exception e)
        {
            answer.setText("Waiting for Answer...");
        }

        //Delete
        if(Main.loggedIn && (Main.loggedInPerson.getId().equals(document.get("WriterID"))
                || Main.loggedInPerson.getRole().equals("Admin")))
        {
            final Button delete = new Button();
            HBox alignmentBox = new HBox();
            alignmentBox.getChildren().add(delete);
            alignmentBox.setAlignment(Pos.TOP_RIGHT);
            vBox.getChildren().add(alignmentBox);

            delete.setText("X");
            delete.setStyle("-fx-background-color: red;" + "-fx-font-size: 5;" + "-fx-font-weight: bold");
            delete.setAlignment(Pos.TOP_RIGHT);

            delete.setOnAction(new EventHandler<ActionEvent>()
            {
                public void handle(ActionEvent e)
                {
                    writerID = (ObjectId) document.get("WriterID");
                    deleteRating();
                }
            });

        }

        //Build HBox
        vBox.getChildren().add(subject);
        vBox.getChildren().add(complain);
        vBox.getChildren().add(answer);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.TOP_LEFT);
        hBox.getChildren().add((vBox));

        return hBox;
    }

    private void deleteRating()
    {

        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Help");

            BasicDBObject criteria = new BasicDBObject();
            criteria.append("_id", comlainID);
            Document rating = collection.find(criteria).first();

            collection.deleteOne(rating);
            openHelpCenter();
        }
        catch (Exception e){System.out.print(e + "\n");}


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

    @FXML
    public void loadHelp() throws Exception
    {
        if (Main.loggedIn)
        {
            try
            {
                URL url = new File("src/main/java/FXML/NewHelpEntryFXML.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                HomePageController.open_stage.setScene(new Scene(root));
                HomePageController.open_stage.setTitle("HelpCenter");
                HomePageController.open_stage.show();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.print("Login first!\n");
        }
    }

}
