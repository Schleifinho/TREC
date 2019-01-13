package Controller;

import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class LoginController
{
    String Email = "";
    String Password = "";

    @FXML private TextField id_email;
    @FXML private TextField id_password;

    public void login()
    {
        Email = id_email.getText();
        Password = id_password.getText();

        MongoClient mongoClient = MongoClients.create();

        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("User");


            Document user = collection.find(eq("Email", Email)).first();
            String pass = user.getString("Password");

            if(Password.equals(pass))
            {
                System.out.println("Logged IN\n");

                Main.loggedIn = true;

                Main.loggedInPerson.setId(user.getObjectId("_id"));
                Main.loggedInPerson.setName(user.getString("Name"));
                Main.loggedInPerson.setSurname(user.getString("Surname"));
                Main.loggedInPerson.setEmail(user.getString("Email"));
                Main.loggedInPerson.setPassword(user.getString("Password"));
                Main.loggedInPerson.setDate(user.getDate("Date"));
                Main.loggedInPerson.setRole(user.getString("Role"));
                Main.loggedInPerson.setInterests((List<String>)user.get("Interests"));

                Stage stage = (Stage) id_email.getScene().getWindow();
                stage.close();
            }
            else
            {
                System.out.println("Login failed!");
            }
        }
        catch (Exception e)
        {
            System.out.println("Login failed!");
        }
        finally
        {
            mongoClient.close();
        }
    }
}
