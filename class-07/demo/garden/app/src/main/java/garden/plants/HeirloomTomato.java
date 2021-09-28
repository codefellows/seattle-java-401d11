package garden.plants;

public class HeirloomTomato<T> extends Tomato<T>
{
    @Override
    public void trimExcess()
    {
        System.out.println("Please be careful!");
    }
}
