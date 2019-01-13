package Controller;

import Objects.Address;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.beans.Expression;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class HotelPageEditController implements Initializable
{
    @FXML private Label id_title;
    @FXML private TextField id_hotelname;
    @FXML private TextField id_address;
    @FXML private TextField id_location;
    @FXML private TextField id_number;
    @FXML private TextField id_lowprice;
    @FXML private TextField id_maxprice;
    @FXML private TextArea id_description;
    @FXML private CheckBox id_tennis;
    @FXML private CheckBox id_swim;
    @FXML private CheckBox id_ski;
    @FXML private CheckBox id_eat;
    @FXML private CheckBox id_sea;
    @FXML private CheckBox id_hiking;
    @FXML private CheckBox id_sauna;
    @FXML private CheckBox id_spa;
    @FXML private CheckBox id_solarium;

    @FXML private CheckBox id_wlan;
    @FXML private CheckBox id_tv;
    @FXML private CheckBox id_pets;
    @FXML private CheckBox id_park;
    @FXML private CheckBox id_restaurant;
    @FXML private CheckBox id_bar;
    @FXML private CheckBox id_ac;
    @FXML private CheckBox id_warm;
    @FXML private CheckBox id_safe;
    @FXML private CheckBox id_pool;
    @FXML private CheckBox id_24h;
    @FXML private CheckBox id_nosmoke;

    private ObjectId ownerID = null;
    private String hotelname = "";
    private String street = "";
    private String location = "";
    private Integer number = 0;
    private Address address = new Address();
    private Integer lowprice = 0;
    private Integer maxprice = 0;
    private String description = "";

    private String[] Interests = new String[9];
    private String[] Facilities = new String[12];

    public static ObjectId hotelID;

    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("Hotel");


            Document user = collection.find(eq("_id", hotelID)).first();
            id_hotelname.setText(user.get("Hotelname").toString());

            List<String> temp_address = (List<String>)user.get("Address");

            id_address.setText(temp_address.get(0));
            id_number.setText(temp_address.get(1));
            id_location.setText(temp_address.get(2));

            id_lowprice.setText(user.get("Lowprice").toString());
            id_maxprice.setText(user.get("Maxprice").toString());
            id_description.setText(user.get("Description").toString());

            List<String> temp_interests = (List<String>)user.get("Interests");
            selectCheckBox(temp_interests);

            List<String> temp_facilities = (List<String>)user.get("Facilities");
            selectCheckBox(temp_facilities);

        }
        catch (Exception e){System.out.print(e + "\n");}
        finally
        {
            mongoClient.close();
        }
    }

    private void selectCheckBox(List<String> temp)
    {
        for(int i = 0; i < temp.size();i++)
        {
            if(temp.get(i) != null)
            {
                setCheckbox(temp.get(i));
            }

        }
    }

    private void setCheckbox(String checkbox)
    {
        //Interests
        if(checkbox.equals("Tennis"))
            id_tennis.setSelected(true);
        if(checkbox.equals("Swim"))
            id_swim.setSelected(true);
        if(checkbox.equals("Ski"))
            id_ski.setSelected(true);
        if(checkbox.equals("Eat"))
            id_eat.setSelected(true);
        if(checkbox.equals("Hiking"))
            id_hiking.setSelected(true);
        if(checkbox.equals("Sea"))
            id_sea.setSelected(true);
        if(checkbox.equals("Sauna"))
            id_sauna.setSelected(true);
        if(checkbox.equals("SPA"))
            id_spa.setSelected(true);
        if(checkbox.equals("Solarium"))
            id_solarium.setSelected(true);

        //Facilities
        if (checkbox.equals("Wlan"))
            id_wlan.setSelected(true);
        if (checkbox.equals("TV"))
            id_tv.setSelected(true);
        if (checkbox.equals("Pets"))
            id_pets.setSelected(true);
        if (checkbox.equals("Park"))
            id_park.setSelected(true);
        if (checkbox.equals("Restaurant"))
            id_restaurant.setSelected(true);
        if (checkbox.equals("Bar"))
            id_bar.setSelected(true);
        if (checkbox.equals("AC"))
            id_ac.setSelected(true);
        if (checkbox.equals("Warm"))
            id_warm.setSelected(true);
        if (checkbox.equals("Safe"))
            id_safe.setSelected(true);
        if (checkbox.equals("Pool"))
            id_pool.setSelected(true);
        if (checkbox.equals("24h"))
            id_24h.setSelected(true);
        if (checkbox.equals("NoSmoke"))
            id_nosmoke.setSelected(true);
    }

    @FXML
    private void saveData()
    {
        boolean valid = false;

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");
        Bson query = null;
        try
        {
            valid = getData();
        }
        catch (Exception e){System.out.print(e);}

        if(valid)
        {
            try
            {
                Bson filter = new Document("_id", hotelID);

                String temp_address[] = new String[3];
                temp_address[0] = address.getStreet();
                temp_address[1] = address.getNumber().toString();
                temp_address[2] = address.getCity();

                updateCollection(collection, filter, "Hotelname", hotelname);
                updateCollection(collection, filter, "Address", Arrays.asList(temp_address));
                updateCollection(collection, filter, "Description", description );
                updateCollection(collection, filter, "Lowprice", lowprice.toString());
                updateCollection(collection, filter, "Maxprice", maxprice.toString());
                updateCollection(collection, filter, "Interests", Arrays.asList(Interests));
                updateCollection(collection, filter, "Facilities", Arrays.asList(Facilities));

                openHotelPage();

            } catch (Exception e)
            {
            } finally
            {
                mongoClient.close();
            }
        }
        else
        {
            System.out.print("Failed\n");
        }
    }

    private void updateCollection(MongoCollection<Document> collection, Bson filter, String key, String value)
    {
        Bson newValue = new Document(key, value);
        Bson updateOperationDocument = new Document("$set", newValue);
        collection.updateOne(filter, updateOperationDocument);
    }

    private void updateCollection(MongoCollection<Document> collection, Bson filter, String key, List value)
    {
        Bson newValue = new Document(key, value);
        Bson updateOperationDocument = new Document("$set", newValue);
        collection.updateOne(filter, updateOperationDocument);
    }

    private void openHotelPage()
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

    private boolean getData()
    {
        RegisterController.checkSelection(Interests, id_tennis, id_swim, id_ski, id_eat, id_hiking, id_sea, id_sauna, id_spa, id_solarium);
        checkFacilities();

        ownerID = Main.loggedInPerson.getId();
        if (ownerID == null)
            return false;

        hotelname = id_hotelname.getText();
        if (hotelname.isEmpty())
            return false;
        street = id_address.getText();
        if (street.isEmpty())
            return false;

        location = id_location.getText();
        if(location.isEmpty())
            return false;

        try{
            number = Integer.parseInt(id_number.getText());
        }catch(Exception e)
        {
            return false;
        }

        address.setStreet(street);
        address.setCity(location);
        address.setNumber(number);

        try{
            lowprice = Integer.parseInt(id_lowprice.getText());
        }catch(Exception e)
        {
            return false;
        }

        try{
            maxprice = Integer.parseInt(id_maxprice.getText());
        }catch(Exception e)
        {
            return false;
        }


        description = id_description.getText();
        if(description.isEmpty())
            return false;

        return true;
    }

    private void checkFacilities()
    {
        if (id_wlan.isSelected())
            Facilities[0] = "Wlan";
        if (id_tv.isSelected())
            Facilities[1] = "TV";
        if (id_pets.isSelected())
            Facilities[2] = "Pets";
        if (id_park.isSelected())
            Facilities[3] = "Park";
        if (id_restaurant.isSelected())
            Facilities[4] = "Restaurant";
        if (id_bar.isSelected())
            Facilities[5] = "Bar";
        if (id_ac.isSelected())
            Facilities[6] = "AC";
        if (id_warm.isSelected())
            Facilities[7] = "Warm";
        if (id_safe.isSelected())
            Facilities[8] = "Safe";
        if (id_pool.isSelected())
            Facilities[9] = "Pool";
        if (id_24h.isSelected())
            Facilities[10] = "24h";
        if (id_nosmoke.isSelected())
            Facilities[11] = "NoSmoke";

    }

    @FXML private void deletePage()
    {
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> hotel_collection = db.getCollection("Hotel");
            Document hotel = hotel_collection.find(eq("_id", hotelID)).first();

            MongoCollection<Document> rating_collection = db.getCollection("Rating");
            List<Document> ratings = (List<Document>) rating_collection.find(eq("HotelID", hotelID)).into(
                    new ArrayList<Document>());

            //Delete Ratings from Hotel
            for (Document rating:ratings)
            {
                rating_collection.deleteOne(rating);
            }

            hotel_collection.deleteOne(hotel);

            //Back to homepage
            Stage stage = (Stage) id_hotelname.getScene().getWindow();
            stage.close();
        }
        catch(Exception e){System.out.print(e + "\n");}
        finally
        {
            mongoClient.close();
        }
    }
}
