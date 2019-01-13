import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;

public class HotelPageController implements Initializable
{

    private Stage hotel_stage;
    private static ObjectId hotelID;
    private static ObjectId ownerID;
    private static ObjectId writerID;

    public static final ObservableList interestsList =
            FXCollections.observableArrayList();
    public static final ObservableList facilitiesList =
            FXCollections.observableArrayList();
    public static final ObservableList ratingList =
            FXCollections.observableArrayList();

    private String Email;
    @FXML
    private Label label_hotelname;
    @FXML
    private Label label_description;
    @FXML
    private Label label_address;
    @FXML
    private ImageView hotel_image;
    @FXML
    private ListView listView_interests;
    @FXML
    private ListView listView_facilities;
    @FXML
    private ListView listView_rating;
    @FXML
    private LineChart lineChart;

    @FXML
    private Button button_edit;

    @FXML
    private VBox vbox_stats;

    public HotelPageController()
    {
        hotel_stage = new Stage();
        hotel_stage.initOwner(HomePageController.open_stage);
        hotel_stage.initModality(Modality.WINDOW_MODAL);
        hotel_stage.setResizable(false);
    }


    public static void initData(ObjectId id)
    {
        hotelID = id;
    }

    public static ObjectId getOwnerID()
    {
        return ownerID;
    }

    public static Object getHotelID()
    {
        return hotelID;
    }

    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Hotel");

            Document hotel = collection.find(eq("_id", hotelID)).first();

            ownerID = (ObjectId) hotel.get("OwnerID");

            System.out.print(Main.loggedInPerson.getId() + " " + ownerID + "\n");

            if(Main.loggedInPerson.getId() != null && ownerID != null)
            {
                if (Main.loggedInPerson.getId().equals(ownerID) || Main.loggedInPerson.getRole().equals("Admin"))
                {
                    if(Main.loggedIn)
                    {
                        button_edit.setVisible(true);
                        vbox_stats.setVisible(true);
                        fillStats();
                    }

                }
                else
                {
                    vbox_stats.setVisible(false);
                    button_edit.setVisible(false);
                    vbox_stats.getChildren().clear();
                }
            }
            else
            {
                vbox_stats.setVisible(false);
                button_edit.setVisible(false);
                vbox_stats.getChildren().clear();
            }

            label_hotelname.setText(hotel.get("Hotelname").toString());

            List<String> temp = (List<String>)hotel.get("Address");
            label_address.setText(temp.get(0) + " " + temp.get(1) + ", "+ temp.get(2));

            String str_description = hotel.get("Description").toString() + "\nPrice: "
                    + hotel.get("Lowprice").toString() + " - "
                    + hotel.get("Maxprice").toString() + "€";
            label_description.setText(str_description);
            label_description.setMinHeight(str_description.length() / 45 * 17 + 34);


            ObjectId imageID = (ObjectId) hotel.get("ImageID");
            String image_url = getFile(imageID);

            try
            {
                Image image = new Image(image_url);
                hotel_image.setImage(image);
                hotel_image.setFitWidth(300);
                hotel_image.setPreserveRatio(true);
                hotel_image.setStyle("-fx-opacity: 1");

            }
            catch (Exception e)
            {
                Image image = new Image("/image/noimage.jpg");
                hotel_image.setImage(image);
                hotel_image.setFitWidth(300);
                hotel_image.setPreserveRatio(true);
                hotel_image.setStyle("-fx-opacity: 1");
                System.out.print("No Image Found!\n"+ e +"\n");
            }

            fillListView(hotel, "Interests", listView_interests, interestsList, 3);
            fillListView(hotel, "Facilities", listView_facilities, facilitiesList, 4);

        } catch (Exception e)
        {
            System.out.print(e);
        } finally
        {
            mongoClient.close();
        }

        fillRatings();
    }

    private String getFile(ObjectId imageID)
    {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase myDatabase = mongoClient.getDatabase("OAD");

        // Create a gridFSBucket using the default bucket name "fs"
        GridFSBucket gridFSBucket = GridFSBuckets.create(myDatabase);
        try
        {
            FileOutputStream streamToDownloadTo = new FileOutputStream(new File("./src/main/resources/image/" + imageID.toString()+".jpg"));
            gridFSBucket.downloadToStream(imageID, streamToDownloadTo);
            streamToDownloadTo.close();
            System.out.println(streamToDownloadTo.toString());
        } catch (IOException e)
        {
            // handle exception
        }

        return "/image/" + imageID.toString()+".jpg";

    }

    @FXML private Label label_rating;

    private void fillStats()
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Rating");
            List<Document> documents = (List<Document>) collection.find(eq("HotelID", hotelID)).into(
                    new ArrayList<Document>());

            double star = 0;
            int counter = 0;
            for (Document document : documents)
            {
                System.out.print(star +"\n");
                star += Double.parseDouble(document.get("Stars").toString());
                counter++;
            }

            star /= counter;

            System.out.print(star +"\n");
            label_rating.setText(String.valueOf(star));

        }
        catch(Exception e){System.out.print(e + " \n");}
        finally
        {
            mongoClient.close();
        }
    }


    private void fillRatings()
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Rating");

            List<Document> documents = (List<Document>) collection.find(eq("HotelID", hotelID)).into(
                    new ArrayList<Document>());

            ratingList.clear();

            int size = 0;
            for (final Document document : documents)
            {
                VBox entry = new VBox();
                entry.setAlignment(Pos.CENTER);
                entry.setFillWidth(true);

                if(Main.loggedIn && (Main.loggedInPerson.getId().equals(document.get("WriterID"))
                        || Main.loggedInPerson.getRole().equals("Admin")))
                {
                    System.out.print("My rating\n");
                    final Button delete = new Button();
                    HBox alignmentBox = new HBox();
                    alignmentBox.getChildren().add(delete);
                    alignmentBox.setAlignment(Pos.TOP_RIGHT);
                    entry.getChildren().add(alignmentBox);

                    delete.setText("X");
                    delete.setStyle("-fx-background-color: red;" + "-fx-font-size: 5;" + "-fx-font-weight: bold");
                    delete.setAlignment(Pos.TOP_RIGHT);
                    size += 15;

                    delete.setOnAction(new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent e)
                        {
                            System.out.print("Delete Clicked\n");
                            writerID = (ObjectId) document.get("WriterID");
                            System.out.print(writerID +"\n");
                            deleteRating();
                        }
                    });

                }

                size += 18;
                Label name = new Label("from " + document.get("Name").toString());
                name.setStyle("-fx-font-weight: Italic ;" + "-fx-font-size: 10;" + "-fx-alignment: right");
                name.setAlignment(Pos.CENTER_RIGHT);

                size += 22;
                Label stars = new Label(document.get("Stars").toString());
                stars.setStyle("-fx-font-weight: bold");
                Label text = new Label(document.get("Text").toString());
                text.setMaxWidth(250);
                text.setWrapText(true);

                size += text.getText().length() / 40 * 20 + 20;

                entry.getChildren().addAll(stars, text, name);
                entry.setSpacing(1);
                ratingList.addAll(entry);
                listView_rating.setItems(ratingList);
            }

            listView_rating.setMinHeight(size);

        } catch (Exception e)
        {
            System.out.print(e + "\n");
        } finally
        {
            mongoClient.close();
        }
    }

    private void deleteRating()
    {

        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Rating");

            BasicDBObject criteria = new BasicDBObject();
            criteria.append("HotelID", hotelID);
            criteria.append("WriterID",  writerID);
            Document rating = collection.find(criteria).first();

            collection.deleteOne(rating);
        }
        catch (Exception e){System.out.print(e + "\n");}


    }

    private void fillListView(Document hotel, String document, ListView listView, ObservableList data, int rows)
    {
        List<String> list = (List<String>) hotel.get(document);
        int counter = 1;
        HBox layout = new HBox();
        layout.setSpacing(40);
        layout.setAlignment(Pos.CENTER);
        VBox context = new VBox();
        context.setFillWidth(true);

        data.clear();

        for (String string : list)
        {
            if (string != null)
            {
                context.getChildren().add(new Label(string));
                if (counter % rows == 0)
                {
                    layout.getChildren().add(context);
                    context = new VBox();
                }
            }

            if (string != null)
            {
                counter++;
            }
        }

        double height = 20 * rows;

        if (counter <= rows)
        {
            layout.getChildren().add(context);
            height = 25 * (counter - 1);
        }


        data.addAll(layout);
        listView.setItems(data);
        listView.setMinHeight(height);
    }

    @FXML
    private void contactOwner()
    {
        try
        {
            URL url = new File("src/main/java/FXML/ContactFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);

            hotel_stage.setScene(new Scene(root));
            hotel_stage.setTitle("Contact");
            hotel_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void bewerten() throws Exception
    {

        try
        {
            URL url = new File("src/main/java/FXML/BewertungenFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);

            hotel_stage.setScene(new Scene(root));
            hotel_stage.setTitle("Bewerten");
            hotel_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void editHotelPage()
    {
        HotelPageEditController.hotelID = hotelID;
        try
        {
            URL url = new File("src/main/java/FXML/HotelPageEditFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            HomePageController.open_stage.setScene(new Scene(root));
            HomePageController.open_stage.setTitle("Edit Hotel Page");
            HomePageController.open_stage.show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}


