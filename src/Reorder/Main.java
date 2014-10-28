package Reorder;

import java.util.List;


public class Main
{
    public static void main(String[] args)
    {
        List<IReorderModule> reorder_modules_to_run =
            ReorderModuleProducer.parse_reorder_args(args);
    }
}