import Objects.Person;
import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class RegisterController
{
    private String Name = "";
    private String Surname = "";
    private String Email = "";
    private String Password = "";
    private String Password_Confirm = "";
    private String Role = "User";
    private Date Date_of_Birth;
    private String[] Interests = new String[9];

    public void register() throws IOException
    {
        boolean valid = false;

        MongoClient mongoClient = MongoClients.create();
        MongoDatabase db = mongoClient.getDatabase("OAD");
        MongoCollection<Document> collection = db.getCollection("User");
        try
        {
            valid = getData(collection);
        }
        catch (Exception e){System.out.print(e);}

        if(valid)
        {
            try
            {
                Document user = new Document();
                user.put("Name", Name);
                user.put("Surname", Surname);
                user.put("Date", Date_of_Birth);
                user.put("Email", Email);
                user.put("Password", Password);
                user.put("Role", "User");
                user.put("Interests", Arrays.asList(Interests));

                collection.insertOne(user);


            } catch (Exception e) {}
            finally
            {
                mongoClient.close();
            }

            Main.loggedIn = true;
            Main.loggedInPerson.setName(Name);
            Main.loggedInPerson.setSurname(Surname);
            Main.loggedInPerson.setEmail(Email);
            Main.loggedInPerson.setPassword(Password);
            Main.loggedInPerson.setDate(Date_of_Birth);

            Stage stage = (Stage) id_name.getScene().getWindow();
            stage.close();

            System.out.print("Registration success and logged in\n");
        }
        else
        {
            System.out.print("Registration failed\n");
        }
    }

    @FXML private TextField id_name;
    @FXML private TextField id_surname;
    @FXML private TextField id_email;
    @FXML private DatePicker id_date;
    @FXML private TextField id_password;
    @FXML private TextField id_password_confirm;
    @FXML private CheckBox id_tennis;
    @FXML private CheckBox id_swim;
    @FXML private CheckBox id_ski;
    @FXML private CheckBox id_eat;
    @FXML private CheckBox id_sea;
    @FXML private CheckBox id_hiking;
    @FXML private CheckBox id_sauna;
    @FXML private CheckBox id_spa;
    @FXML private CheckBox id_solarium;


    private boolean getData(MongoCollection<Document> collection)
    {
        Name = id_name.getText();
        Surname = id_surname.getText();
        Email = id_email.getText();
        Password = id_password.getText();
        Password_Confirm = id_password_confirm.getText();
        Date_of_Birth = Date.from(id_date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        checkBoxes();

        if(Name.isEmpty() || Surname.isEmpty() || Date_of_Birth.toString().isEmpty() || Email.isEmpty() || Password.isEmpty() || Password_Confirm.isEmpty())
        {
            System.out.print("Empty\n");
            return false;
        }

        try
        {
            Document user = collection.find(eq("Email", Email)).first();

            if(Email.equals(user.getString("Email")))
            {
                System.out.print("Email already exists!\n");
                return false;
            }
        }
        catch (Exception e){System.out.print(e);}

        if(!Password.equals(Password_Confirm))
        {
            System.out.print("Password doesn't equals\n");
            return false;
        }


        return true;
    }

    private void checkBoxes()
    {
        checkSelection(Interests, id_tennis, id_swim, id_ski, id_eat, id_hiking, id_sea, id_sauna, id_spa, id_solarium);
    }

    static void checkSelection(String[] interests, CheckBox id_tennis, CheckBox id_swim, CheckBox id_ski, CheckBox id_eat, CheckBox id_hiking, CheckBox id_sea, CheckBox id_sauna, CheckBox id_spa, CheckBox id_solarium)
    {
        if(id_tennis.isSelected())
        {
            interests[0] = "Tennis";
        }

        if(id_swim.isSelected())
        {
            interests[1] = "Swim";
        }

        if(id_ski.isSelected())
        {
            interests[2] = "Ski";
        }

        if(id_eat.isSelected())
        {
            interests[3] = "Eat";
        }

        if(id_hiking.isSelected())
        {
            interests[4] = "Hiking";
        }

        if(id_sea.isSelected())
        {
            interests[5] = "Sea";
        }

        if(id_sauna.isSelected())
        {
            interests[6] = "Sauna";
        }

        if(id_spa.isSelected())
        {
            interests[7] = "SPA";
        }

        if(id_solarium.isSelected())
        {
            interests[8] = "Solarium";
        }
    }
}
