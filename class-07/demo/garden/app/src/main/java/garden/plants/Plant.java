package garden.plants;

import garden.interfaces.Waterable;
import garden.tools.WateringHose;

import java.util.ArrayList;

public class Plant<T> implements Waterable
{
    public String color;
    String purpose;
    int height;
    boolean isEdible;
    T description;

    public void water(int waterInOz, WateringHose hose)
    {
        System.out.println("woosh woosh");
        waterInOz = 10;
        hose.color = "Brown";
    }

    public T getDescription()
    {
        return description;
    }

    public void setDescription(T description)
    {
        this.description = description;
    }
}
