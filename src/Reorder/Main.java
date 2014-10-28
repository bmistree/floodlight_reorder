package Reorder;

import java.util.List;


public class Main
{
    public static void main(String[] args)
    {
        List<IReorderModule> reorder_modules_to_run =
            ReorderModuleProducer.parse_reorder_args(args);

        ReorderRunner reorder_runner =
            new ReorderRunner(reorder_modules_to_run);

        if (reorder_runner.try_to_reorder())
            System.out.println("\n\nGot a reordering\n\n");
        else
            System.out.println("\n\nNo reordering\n\n");
    }
}