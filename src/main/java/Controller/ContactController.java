import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.ZoneId;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import static com.mongodb.client.model.Filters.eq;

public class ContactController
{
    private String HotelOwnerEmail;
    private String MyEmail;

    @FXML private DatePicker date_from;
    @FXML private DatePicker date_to;
    @FXML private TextArea text;
    public void sendEmail()
    {
        // Recipient's email ID needs to be mentioned.
        String to = "abcd@gmail.com";


        String from = "web@gmail.com";

        // Sender's email ID needs to be mentioned

        if (Main.loggedIn)
        {


            MyEmail = Main.loggedInPerson.getEmail();
            ObjectId id = HotelPageController.getOwnerID();
            HotelOwnerEmail = getOwnerEmail(id);

            String str_from = "";
            String str_to = "";

            System.out.print(date_from.getValue());
            if(date_from.getValue() != null && date_to.getValue() != null)
            {
                str_from =  Date.from(date_from.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).toString();
                str_to =  Date.from(date_to.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).toString();


                String message_text = "Hi,\n" +
                        "I want to book a room from " + str_from +
                        " to " + str_to + "\n" +
                        text + "\n" +
                        "your sincerely\n" +
                        Main.loggedInPerson.getName() + " " + Main.loggedInPerson.getSurName();

                System.out.print("From: " + MyEmail + "\n");
                System.out.print("To: " + HotelOwnerEmail + "\n");
                System.out.print("Subject: reservation from " + Main.loggedInPerson.getName() + "!" + "\n");
                System.out.print(message_text + "\n");
                System.out.print("Email sent...\n");
            }
            else
            {
                System.out.print("Please choose a date!\n");

            }
        }
        /*
        // Assuming you are sending email from localhost
        String host = "localhost";
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        // Get the default Session object.
        Session session = Session.getInstance(properties, null);
        try
        {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(MyEmail));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(HotelOwnerEmail));

            // Set Subject: header field
            message.setSubject("reservation from " + Main.loggedInPerson.getName() + "!");

            // Now set the actual message

            message.setText("This is actual message");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
*/
    }

    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("no_reply@example.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOwnerEmail(ObjectId id)
    {
        String email = "";
        MongoClient mongoClient = MongoClients.create();
        try
        {
            MongoDatabase db = mongoClient.getDatabase("OAD");
            MongoCollection<Document> collection = db.getCollection("User");


            Document user = collection.find(eq("_id", id)).first();

            System.out.print(user);
            email = user.get("Email").toString();

        }
        catch (Exception e){System.out.print(e + "OLA\n");}
        finally
        {
            mongoClient.close();
        }

        return email;
    }
}
