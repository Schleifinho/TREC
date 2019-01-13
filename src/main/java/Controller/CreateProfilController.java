package Controller;

import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;

public class CreateProfilController implements Initializable
{

    @FXML private TextField id_old_password;
    @FXML private TextField id_new_password;
    @FXML private TextField id_new_password_confirm;
    @FXML private TextField id_name;
    @FXML private TextField id_email;
    @FXML private TextField id_surname;
    @FXML private CheckBox id_tennis;
    @FXML private CheckBox id_swim;
    @FXML private CheckBox id_ski;
    @FXML private CheckBox id_eat;
    @FXML private CheckBox id_sea;
    @FXML private CheckBox id_hiking;
    @FXML private CheckBox id_sauna;
    @FXML private CheckBox id_spa;
    @FXML private CheckBox id_solarium;

    private String Email = "";
    private String Password = "";
    private String Password_Confirm = "";
    private String[] Interests = new String[9];


    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        System.out.print("Edit Profil\n");
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("User");

            Document user = collection.find(eq("Email", Main.loggedInPerson.getEmail())).first();

            //Fill personal Data
            id_name.setText(user.getString("Name"));
            id_surname.setText(user.getString("Surname"));
            id_email.setText(user.getString("Email"));


            //Fill Interests
            List<String> test = (List<String>)user.get("Interests");

            for(int i = 0; i < test.size();i++)
            {
                System.out.print(test.get(i));
                if(test.get(i) != null)
                    setCheckbox(test.get(i));

            }
        }
        catch (Exception e) {}
        finally
        {
            mongoClient.close();
        }
    }

    private void setCheckbox(String checkbox)
    {
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
    }

    public void saveData()
    {
        if(id_old_password.getText().equals(Main.loggedInPerson.getPassword()))
        {
            getData();
            MongoClient mongoClient = MongoClients.create();
            try
            {
                MongoDatabase db = mongoClient.getDatabase("OAD");
                MongoCollection<Document> collection = db.getCollection("User");

                if(!Email.isEmpty())
                {
                    setEmail(collection);
                }
                if(!Password.isEmpty() && Password.equals(Password_Confirm))
                {
                    setPassword(collection);
                }

                setInterests(collection);

                Stage stage = (Stage) id_email.getScene().getWindow();
                stage.close();

            }
            catch (Exception e) {}
            finally
            {
                mongoClient.close();
            }
        }
        else
        {
            System.out.print("Wrong Password!\n");
        }
    }

    private void setInterests(MongoCollection<Document> collection)
    {
        Bson filter = new Document("Email", Main.loggedInPerson.getEmail());
        Bson newValue = new Document("Interests", Arrays.asList(Interests));
        Bson updateOperationDocument = new Document("$set", newValue);
        collection.updateOne(filter, updateOperationDocument);
    }

    private void setEmail(MongoCollection<Document> collection)
    {
        try
        {
            Bson filter = new Document("Email", Main.loggedInPerson.getEmail());
            Bson newValue = new Document("Email", Email);
            Bson updateOperationDocument = new Document("$set", newValue);
            collection.updateOne(filter, updateOperationDocument);
            Main.loggedInPerson.setEmail(Email);

        }
        catch (Exception e)
        {
            System.out.print(e);
        }
    }

    private void setPassword(MongoCollection<Document> collection)
    {
        try
        {
            Bson filter = new Document("Email", Main.loggedInPerson.getEmail());
            Bson newValue = new Document("Password", Password);
            Bson updateOperationDocument = new Document("$set", newValue);
            collection.updateOne(filter, updateOperationDocument);
            Main.loggedInPerson.setPassword(Password);
        }
        catch (Exception e)
        {
            System.out.print(e);
        }
    }



    private void getData()
    {
        Password = id_new_password.getText();
        Password_Confirm = id_new_password_confirm.getText();
        checkBoxes();
    }

    private void checkBoxes()
    {
        RegisterController.checkSelection(Interests, id_tennis, id_swim, id_ski, id_eat, id_hiking, id_sea, id_sauna, id_spa, id_solarium);
    }

    @FXML
    private void deleteAccount()
    {
        if(id_old_password.getText().equals(Main.loggedInPerson.getPassword()))
        {
            MongoClient mongoClient = MongoClients.create();
            try
            {
                MongoDatabase db = mongoClient.getDatabase("OAD");
                MongoCollection<Document> collection = db.getCollection("User");

                Document user = collection.find(eq("Email", Main.loggedInPerson.getEmail())).first();

                collection.deleteOne(user);

                System.out.print("Deleted Account");
                Main.loggedIn = false;
                Main.loggedInPerson.clear();
                Stage stage = (Stage) id_email.getScene().getWindow();
                stage.close();
            }
            catch (Exception e){System.out.print(e);}
            finally
            {
                mongoClient.close();
            }
        }
    }
}
