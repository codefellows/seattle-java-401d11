package garden;

public class Description
{
    String descriptionString;
    int quantity;

    public Description(String descriptionString, int quantity)
    {
        this.descriptionString = descriptionString;
        this.quantity = quantity;
    }

    @Override
    public String toString()
    {
        return descriptionString + " and there are " + quantity + " of me";
    }
}
