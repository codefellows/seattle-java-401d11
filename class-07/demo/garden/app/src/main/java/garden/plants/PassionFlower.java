package garden.plants;

import garden.interfaces.Shearable;

public class PassionFlower<T> extends Plant<T> implements Shearable
{
    String flowerColor;

    @Override
    public void trimExcess()
    {
        System.out.println("Snip snip");
    }
}
