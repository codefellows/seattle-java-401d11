package garden.plants;

import garden.interfaces.Shearable;

public class Tomato<T> extends Plant<T> implements Shearable
{
    String kind;

    public Tomato()
    {
        color = "Red";
    }

    @Override
    public void trimExcess()
    {
        snip();
    }

    private void snip()
    {
        System.out.println("Snip snip");
    }
}
