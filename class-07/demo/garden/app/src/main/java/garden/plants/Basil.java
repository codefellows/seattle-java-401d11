package garden.plants;

import garden.interfaces.Shearable;

public class Basil extends Plant implements Shearable
{
    String kind;

    @Override
    public void trimExcess()
    {
        System.out.println("Snip snip");
    }
}
