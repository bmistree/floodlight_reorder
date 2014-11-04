package OneZero;

import java.util.List;
import Reorder.IReorderModule;
import Reorder.ReorderModuleProducer;
import Reorder.ReorderRunner;

public class Main
{
    public static void main(String[] args)
    {
        List<IReorderModule> reorder_modules_to_run =
            ReorderModuleProducer.INSTANCE.parse_reorder_args(args);

        ReorderRunner reorder_runner =
            new ReorderRunner(
                reorder_modules_to_run,
                OneZeroProtocolUtil.INSTANCE);

        if (reorder_runner.try_to_reorder())
            System.out.println("\n\nGot a reordering\n\n");
        else
            System.out.println("\n\nNo reordering\n\n");
    }
}