package Objects;

import org.bson.types.ObjectId;

public class Hotel
{
    private ObjectId OwnerID;
    private String Hotelname = "";
    private Address Hoteladdress = new Address();
    private int lowPrice;
    private int maxPrice;
    private String Description = "";
    private String[] Facilities = new String[12];
    private String[] Interests = new String[9];

    public ObjectId getOwnerID()
    {
        return OwnerID;
    }

    public void setOwnerID(ObjectId ownerID)
    {
        this.OwnerID = ownerID;
    }

    public String getHotelname()
    {
        return Hotelname;
    }

    public void setHotelname(String hotelname)
    {
        this.Hotelname = hotelname;
    }

    public Address getAdress()
    {
        return Hoteladdress;
    }

    public void setAdress(Address address)
    {
        this.Hoteladdress.setCity(address.getCity());
        this.Hoteladdress.setNumber(address.getNumber());
        this.Hoteladdress.setStreet(address.getStreet());
    }

    public int getLowPrice()
    {
        return lowPrice;
    }

    public void setLowPrice(int price)
    {
        this.lowPrice = price;
    }

    public int getMaxPrice()
    {
        return maxPrice;
    }

    public void setMaxPrice(int price)
    {
        this.maxPrice = price;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        this.Description = description;
    }

    public String[] getFacilities()
    {
        return Facilities;
    }

    public void setFacilities(String[] facilities)
    {
        for (int i = 0; i < this.Facilities.length; i++)
        {
            this.Facilities[i] = facilities[i];
        }
    }

    public String[] getInterests()
    {
        return Facilities;
    }

    public void setInterests(String[] interests)
    {
        for (int i = 0; i < this.Interests.length; i++)
        {
            this.Interests[i] = interests[i];
        }
    }

    public Hotel(){}

    public Hotel(ObjectId oID, String name, Address address, int lowprice, int maxprice,
                 String description, String[] interests, String[] facilities)
    {
        this.OwnerID = oID;
        this.Hotelname = name;
        this.Hoteladdress.setCity(address.getCity());
        this.Hoteladdress.setNumber(address.getNumber());
        this.Hoteladdress.setStreet(address.getStreet());
        this.lowPrice = lowprice;
        this.maxPrice = maxprice;
        this.Description = description;

        for (int i = 0; i < this.Facilities.length; i++)
        {
            this.Facilities[i] = facilities[i];
        }

        for (int i = 0; i < this.Interests.length; i++)
        {
            this.Interests[i] = interests[i];
        }

    }
}
