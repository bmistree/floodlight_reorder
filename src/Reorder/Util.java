package Reorder;


public class Util
{
    public static void force_assert(String reason)
    {
        System.err.println(reason);
        assert(false);
        System.exit(-1);
    }
}