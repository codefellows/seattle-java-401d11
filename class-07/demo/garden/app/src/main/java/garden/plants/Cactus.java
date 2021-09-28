package garden.plants;

import garden.tools.WateringHose;

public class Cactus<T> extends Plant<T>
{
    @Override
    public void water(int waterInOz, WateringHose hose)
    {
        System.out.println("Hey! I don't really need this");
    }
}
