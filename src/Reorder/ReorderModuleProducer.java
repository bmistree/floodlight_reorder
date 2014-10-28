package Reorder;

import java.util.Map;
import java.util.HashMap;

public class ReorderModuleProducer
{
    private static final Map<String,IReorderModuleFactory> reorder_modules =
        new HashMap<String,IReorderModuleFactory>();

    public static void add_reorder_module(
        String module_name, IReorderModuleFactory factory)
    {
        reorder_modules.put(module_name,factory);
    }
    
    public static IReorderModule get_module(String module_name)
    {
        if (! reorder_modules.containsKey(module_name))
            return null;

        IReorderModuleFactory factory = reorder_modules.get(module_name);
        return factory.construct();
    }
}