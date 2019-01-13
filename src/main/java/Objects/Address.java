package Objects;

import java.util.Date;

public class Address
{
    private String street;
    private String city;
    private int number;


    public String getStreet()
    {
        return street;
    }

    public void setStreet(String new_street)
    {
        this.street = new_street;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String new_city)
    {
        this.city = new_city;
    }

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(int new_number)
    {
        this.number = new_number;
    }


    public Address(String new_street, String new_city, int new_number)
    {
        this.street = new_street;
        this.city = new_city;
        this.number = new_number;
    }

    public Address()
    {
    }
}
