public class Main2
{
    // Code always runs from this!
    public static void main(String[] args)
    {
        char char1 = 'a';
        char char2 = 'b';
        int int1 = 5;
        long long1 = 100;
        byte byte1 = (byte)0xFF;
        short short1 = (short)0xFFFF;
        boolean boolean1 = true;
        double double1 = 0.1;
        double double2 = 0.2;
        double double3 = 0.3;
        //double double1 = 0.2f;
        String string1 = new String("hello");
        string1 = "hello";

        int[] intArray1 = {3, 2};

        for(int i = 0; i < intArray1.length; i++)
        {
            System.out.println(intArray1[i]);
            break;
        }

        int counter = 0;
        while (counter < intArray1.length)
        {
            System.out.println(intArray1[counter]);
            counter++;
            break;
        }
        for(int currentInt : intArray1)
        {
            System.out.println(currentInt);
        }

        int newInt = 5;
        newInt = returnInt(3);
        System.out.println(newInt);
    }

    // public static TYPEOFRETURN NAMEOFFUNCTION(ARGUMENTS)
    public static int returnInt(int intArg)
    {
        return intArg;
    }
}