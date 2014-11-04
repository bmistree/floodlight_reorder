package Reorder;

public class Util
{
    public static void force_assert(String reason)
    {
        System.err.println(reason);
        assert(false);
        System.exit(-1);
    }
    
    public static void log_warn(String message)
    {
        System.out.println(message);
    }

    public static void log_info(ILoggable loggable, String msg)
    {
        System.out.println(
            "[" + loggable.loggable_module_name() + "] " + msg);
    }
    
}