import Objects.Address;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class CreateHotelPageController
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

    private File selectedFile = new File("");

    @FXML
    public void createHotelPage() throws Exception
    {
        boolean valid = false;

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("Hotel");
        try
        {
            valid = getData();
        } catch (Exception e)
        {
            System.out.print(e);
        }

        if (valid)
        {
            try
            {
                String temp_address[] = new String[3];
                temp_address[0] = address.getStreet();
                temp_address[1] = address.getNumber().toString();
                temp_address[2] = address.getCity();

                Document user = new Document();
                user.put("OwnerID", ownerID);
                user.put("Hotelname", hotelname);
                user.put("Address", Arrays.asList(temp_address));
                user.put("Lowprice", lowprice);
                user.put("Maxprice", maxprice);
                user.put("Description", description);
                user.put("Interests", Arrays.asList(Interests));
                user.put("Facilities", Arrays.asList(Facilities));

                ObjectId imageID = saveImage(hotelname);

                user.put("ImageID", imageID);

                collection.insertOne(user);
                System.out.print("Hotel created\n");
                Stage stage = (Stage) id_hotelname.getScene().getWindow();
                stage.close();

            } catch (Exception e)
            {
            } finally
            {
                mongoClient.close();
            }
        } else
        {
            System.out.print("Failed\n");
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
        if (selectedFile.getPath().equals(""))
            return false;

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

    @FXML private ImageView imageView;

    public ObjectId saveImage(String name) throws Exception
    {
       MongoClient mongoClient = MongoClients.create();
       MongoDatabase myDatabase = mongoClient.getDatabase("OAD");

        // Create a gridFSBucket using the default bucket name "fs"
        GridFSBucket gridFSBucket = GridFSBuckets.create(myDatabase);
        ObjectId fileId = new ObjectId();

        try
        {
            InputStream streamToUploadFrom = new FileInputStream(selectedFile);
            // Create some custom options
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(358400)
                    .metadata(new Document("type", "jpg"));

            fileId = gridFSBucket.uploadFromStream(name, streamToUploadFrom, options);
        } catch (FileNotFoundException e)
        {
            System.out.print(e + "\n");// handle exception
        }

        return fileId;
    }

    @FXML private Label image_path;
    @FXML
    private void selectFile()
    {
        FileChooser directoryChooser = new FileChooser();
        selectedFile = directoryChooser.showOpenDialog(Main.active_stage);
        image_path.setText(selectedFile.getPath());

    }
}
