package Objects;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public final class Person
{
    private ObjectId id;
    private String Name = "";
    private String Surname = "";
    private String Email = "";
    private String Password = "";
    private String Role = "";
    private Date Date_of_Birth;
    private List<String> Interests;

    public ObjectId getId()
    {
        return id;
    }

    public void setId(ObjectId ID)
    {
        this.id = ID;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        this.Name = name;
    }

    public String getSurName()
    {
        return Surname;
    }

    public void setSurname(String surname)
    {
        this.Surname = surname;
    }

    public String getEmail()
    {
        return Email;
    }

    public void setEmail(String email)
    {
        this.Email = email;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String password)
    {
        this.Password = password;
    }

    public String getRole()
    {
        return Role;
    }

    public void setRole(String role)
    {
        this.Role = role;
    }

    public Date getDate()
    {
        return Date_of_Birth;
    }

    public void setDate(Date date)
    {
        this.Date_of_Birth = date;
    }

    public List<String> getInterests()
    {
        return Interests;
    }
    public void setInterests(List<String> interests)
    {
        this.Interests = interests;
    }

    public void clear()
    {
        this.Name = null;
        this.Surname = null;
        this.Email = null;
        this.Password = null;
        this.Role = null;
        this.Date_of_Birth = null;
        this.id = null;
        this.Interests = null;
    }

    public Person(String name, String surname, String email, String password, String role, Date date_of_birth)
    {
        this.Name = name;
        this.Surname = surname;
        this.Email = email;
        this.Password = password;
        this.Role = role;
        this.Date_of_Birth = date_of_birth;
    }

    public Person()
    {
    }
}
