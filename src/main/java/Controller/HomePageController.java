package Controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;


public class HomePageController implements Initializable
{
    static Stage open_stage;
    @FXML private Button button_login;
    @FXML private ListView listView;
    @FXML private ListView weeklyListView;
    @FXML private Label hotelname;
    public static final ObservableList data =
            FXCollections.observableArrayList();

    public static final ObservableList weekly =
            FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        if (Main.loggedIn)
        {
            button_login.setText("Logout");
        }
        else
        {
            button_login.setText("Login");
        }

        setWeeklyHotel();

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");

        List<Document> documents = (List<Document>) collection.find().into(
                new ArrayList<Document>());

        printHotels(documents);

    }

    private void setWeeklyHotel()
    {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");

        List<Document> hotels = (List<Document>) collection.find().into(
                new ArrayList<Document>());

        Document temp_hotel = null;
        int temp_views = -1;
        for(Document hotel : hotels)
        {
            List<String> stats = (List<String>) hotel.get("Stats");

            int my_views = 0;
            try
            {
                my_views = Integer.parseInt(stats.get(0));
            }
            catch(Exception e)
            {
                System.out.print(e + "\n");
            }

            if (my_views > temp_views)
            {
                temp_hotel = hotel;
                temp_views = my_views;
            }
        }
        if(temp_hotel != null)
        {
            printWeeklyHotel(temp_hotel);
        }
    }

    private void printWeeklyHotel(Document hotel)
    {

        weekly.add(listWeeklyEntry(hotel));
        weeklyListView.setItems(weekly);
    }

    public VBox listWeeklyEntry(Document document)
    {
        VBox vBox = new VBox();
        vBox.setId(document.get("_id").toString());

        ObjectId imageID = (ObjectId) document.get("ImageID");
        String url = getFile(imageID);
        ImageView imageView;
        double height = 0;
        try
        {
            Image image = new Image(url);
            imageView = new ImageView(image);
            imageView.setFitWidth(120);
            imageView.setPreserveRatio(true);
            height = image.getHeight() * 120.0/image.getWidth();

        }
        catch (Exception e)
        {
            Image image = new Image("/image/noimage.jpg");
            imageView = new ImageView(image);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
            System.out.print("No Image Found!\n"+ e +"\n");
        }

        weeklyListView.setPrefHeight(height + 27);

        Label hotelname = new Label(document.get("Hotelname").toString());
        hotelname.setStyle("-fx-font-weight: bold;"+"-fx-font-size: 12");

        vBox.getChildren().add(hotelname);
        vBox.getChildren().add(imageView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(2);

        return vBox;
    }

    public HBox listEntry(Document document)
    {
        HBox hBox = new HBox();
        hBox.setId(document.get("_id").toString());

        ObjectId imageID = (ObjectId) document.get("ImageID");
        String url = getFile(imageID);

        ImageView imageView;
        try
        {
            Image image = new Image(url);
            imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

        }
        catch (Exception e)
        {
            Image image = new Image("/image/noimage.jpg");
            imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            System.out.print("No Image Found!\n"+ e +"\n");
        }


        Label hotelname = new Label(document.get("Hotelname").toString());
        hotelname.setStyle("-fx-font-weight: bold;"+"-fx-font-size: 16");

        List<String> temp_address = (List<String>)document.get("Address");
        Label address = new Label(temp_address.get(0) + " " + temp_address.get(1) + ", "+ temp_address.get(2));
        address.setMaxWidth(200);
        address.setWrapText(true);
        Label price = new Label("\nPrice: " + document.get("Lowprice").toString() + " - " + document.get("Maxprice").toString() +"€");

        //Allgemeines
        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(hotelname, address, price);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        //Description
        Label description = new Label(document.get("Description").toString());
        description.setPrefWidth(200);
        description.setWrapText(true);
        HBox descriptionBox = new HBox();
        descriptionBox.getChildren().add(description);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);


        //Interests + Facilities
        VBox interestsBox = new VBox();
        interestsBox.setAlignment(Pos.CENTER_LEFT);

        Label interestsTitle = new Label("Interests");
        interestsTitle.setStyle("-fx-font-weight: bold;"+"-fx-font-size: 12");

        String temp = "";
        List<String> list = (List<String>)document.get("Interests");
        for (String string : list)
        {
            if (string != null)
                temp += string + ", ";
        }

        if(temp.length() > 2)
            temp = temp.substring(0, temp.length()-2);


        Label interests = new Label(temp);

        Label facilitiesTitle = new Label("\nFacilities");
        facilitiesTitle.setStyle("-fx-font-weight: bold;"+"-fx-font-size: 12");

        list = (List<String>)document.get("Facilities");
        temp = "";
        for (String string : list)
        {
            if (string != null)
                temp += string + ", ";

        }
        if(temp.length() > 2)
            temp = temp.substring(0, temp.length() - 2);

        Label facilities = new Label(temp);

        interestsBox.getChildren().add(interestsTitle);
        interestsBox.getChildren().add(interests);
        interestsBox.getChildren().add(facilitiesTitle);
        interestsBox.getChildren().add(facilities);

        //Build HBox
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add((imageView));
        hBox.getChildren().add((titleBox));
        hBox.getChildren().add((descriptionBox));
        hBox.getChildren().add((interestsBox));

        return hBox;
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
            streamToDownloadTo.close();;
        } catch (IOException e)
        {
            System.out.print(e + "\n");
        }

        return "/image/" + imageID.toString()+".jpg";
    }

    @FXML
    public void onListViewClick(MouseEvent arg)
    {
        if(arg.getClickCount() == 2)
        {
            HBox box = (HBox)listView.getSelectionModel().getSelectedItem();
            if(!box.getId().equals(null))
            {
                loadHotelPage(new ObjectId(box.getId()));
            }
            System.out.print("Double clicked " + listView.getSelectionModel().getSelectedItems().get(0));
        }
    }

    @FXML
    public void onWeeklyViewClick(MouseEvent arg)
    {
        if(arg.getClickCount() == 2)
        {
            VBox box = (VBox)weeklyListView.getSelectionModel().getSelectedItem();
            System.out.print(box + "+++\n");
            if(box != null)
            {
                loadHotelPage(new ObjectId(box.getId()));
            }
            System.out.print("Double clicked " + weeklyListView.getSelectionModel().getSelectedItems().get(0));
        }
    }

    public void loadHotelPage(ObjectId id)
    {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");

        Document hotel = collection.find(eq("_id", id)).first();

        if(hotel != null)
        {
            HotelPageController.initData(id);

            try
            {
                URL url = new File("src/main/java/FXML/HotelPageFXML.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                open_stage.setScene(new Scene(root));
                open_stage.setTitle("Hotel Page");
                open_stage.show();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.print("Hotel not found!\n");
        }
    }

    public HomePageController()
    {
        open_stage = new Stage();
        open_stage.initOwner(Main.active_stage);
        open_stage.initModality(Modality.WINDOW_MODAL);
        open_stage.setResizable(false);
    }

    private void login()
    {
        try
        {
            URL url = new File("src/main/java/FXML/LoginFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            open_stage.setScene(new Scene(root));
            open_stage.setTitle("Login");
            open_stage.showAndWait();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void logout()
    {
        Main.loggedIn = false;
        Main.loggedInPerson.clear();
    }

    @FXML
    private void loginHandler()
    {
        if (Main.loggedIn)
        {
            logout();
            button_login.setText("Login");
        }
        else
        {
            login();
            if (Main.loggedIn)
            {
                button_login.setText("Logout");
                makeRecommendation(Main.loggedInPerson.getInterests());
            }

        }
    }

    @FXML
    private void signUp() throws Exception
    {
        try
        {
            URL url = new File("src/main/java/FXML/RegisterFXML.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            open_stage.setScene(new Scene(root));
            open_stage.setTitle("Register");
            open_stage.showAndWait();
            if (Main.loggedIn)
                button_login.setText("Logout");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void editProfil() throws Exception
    {
        if(Main.loggedIn)
        {
            try
            {
                URL url = new File("src/main/java/FXML/EditProfilFXML.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                open_stage.setScene(new Scene(root));
                open_stage.setTitle("EditProfil " + Main.loggedInPerson.getName());
                open_stage.showAndWait();
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

    public void createHotel() throws Exception
    {
        if(Main.loggedIn)
        {
            try
            {
                URL url = new File("src/main/java/FXML/HotelPageCreateFXML.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                open_stage.setScene(new Scene(root));
                open_stage.setTitle("Create Hotel Page");
                open_stage.show();
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

    @FXML
    public void loadHelpCenter() throws Exception
    {
        if (Main.loggedIn)
        {
            try
            {
                URL url = new File("src/main/java/FXML/HelpCenterFXML.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                open_stage.setScene(new Scene(root));
                open_stage.setTitle("HelpCenter");
                open_stage.show();
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

    @FXML private TextField textfield_search;

    @FXML
    private void search()
    {
        String string_search = textfield_search.getText();

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");

        BasicDBObject query = new BasicDBObject();
        query.put("Hotelname",  Pattern.compile(string_search, Pattern.CASE_INSENSITIVE));
        List<Document> documents = (List<Document>) collection.find(query).into(
                new ArrayList<Document>());

        data.clear();
        printHotels(documents);
    }

    private void printHotels(List<Document> documents)
    {
        data.clear();
        for(Document document : documents)
        {
            hotelname.setText(document.get("Hotelname").toString());

            data.addAll(listEntry(document));

            listView.setItems(data);
        }
    }


    private void makeRecommendation(List<String> interests)
    {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");

        List<Integer> important = new ArrayList<Integer>();

        for(int i = 0; i < interests.size(); i++)
        {
            if(interests.get(i) != null)
            {
                important.add(i);
            }
        }

        List<Document> recommendation = new ArrayList<Document>();
        List<Document> more = collection.find().into(new ArrayList<Document>());

        boolean valid_hotel = false;
        for(Document document:more)
        {
            List<String> hotel_interests = (List<String>) document.get("Interests");

            for(int i = 0; i < important.size(); i++)
            {
                if(interests.get(important.get(i)).equals(hotel_interests.get(important.get(i))))
                {
                    valid_hotel = true;
                }
                else
                {
                    valid_hotel = false;
                    break;
                }
            }

            if (valid_hotel)
            {
                recommendation.add(document);
            }
        }
        if (important.size() == 0)
            printHotels(more);
        else
            printHotels(recommendation);
    }

    @FXML
    private void clicked()
    {
        System.out.print("Clicked");
        makeRecommendation(getAllCheckBoxes());
    }

    @FXML private CheckBox list_1;
    @FXML private CheckBox list_2;
    @FXML private CheckBox list_3;
    @FXML private CheckBox list_4;
    @FXML private CheckBox list_5;
    @FXML private CheckBox list_6;
    @FXML private CheckBox list_7;
    @FXML private CheckBox list_8;
    @FXML private CheckBox list_9;

    private List<String> getAllCheckBoxes()
    {
        String[] my_interests = new String[9];

        if (list_1.isSelected())
            my_interests[0] = "Tennis";
        if (list_2.isSelected())
            my_interests[1] = "Swim";
        if (list_3.isSelected())
            my_interests[2] = "Ski";
        if (list_4.isSelected())
            my_interests[3] = "Eat";
        if (list_5.isSelected())
            my_interests[4] = "Hiking";
        if (list_6.isSelected())
            my_interests[5] = "Sea";
        if (list_7.isSelected())
            my_interests[6] = "Sauna";
        if (list_8.isSelected())
            my_interests[7] = "SPA";
        if (list_9.isSelected())
            my_interests[8] = "Solarium";

        return Arrays.asList(my_interests);
    }
}
