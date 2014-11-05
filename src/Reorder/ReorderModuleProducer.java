package Reorder;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;


public enum ReorderModuleProducer
{
    INSTANCE;
        
    protected final String MODULE_ARG="reorder_modules=";
    
    protected final Map<String,IReorderModuleFactory> reorder_modules =
        new HashMap<String,IReorderModuleFactory>();

    ReorderModuleProducer()
    {
        add_reorder_module(
            SingleThreadAddRemoveModule.REORDER_NAME,
            SingleThreadAddRemoveModule.Factory.INSTANCE);
        add_reorder_module(
            EtherTCPAddRemoveModule.REORDER_NAME,
            EtherTCPAddRemoveModule.Factory.INSTANCE);
    }
    
    public List<IReorderModule> parse_reorder_args(
        String[] args)
    {
        List<IReorderModule> to_return = new ArrayList<IReorderModule>();
        boolean found_arg = false;
        for (String arg : args)
        {
            int index = arg.indexOf(MODULE_ARG);
            if (index != -1)
            {
                String substring = arg.substring(index);
                String[] module_list=substring.trim().split(",");

                for(int i = 0; i < module_list.length; ++i)
                {
                    String module_name = module_list[i].trim();

                    IReorderModule module = get_module(module_name);
                    if (module == null)
                    {
                        Util.force_assert(
                            "Unknown module named '" + module_name + "'." );
                    }
                    to_return.add(module);
                }
                found_arg = true;
                break;
            }
        }

        if (! found_arg)
        {
            // default to running with all modules when not told
            // otherwise.
            for (String module_name : reorder_modules.keySet())
                to_return.add(get_module(module_name));
        }
        
        return to_return;
    }
    
    protected void add_reorder_module(
        String module_name, IReorderModuleFactory factory)
    {
        reorder_modules.put(module_name,factory);
    }
    
    public IReorderModule get_module(String module_name)
    {
        if (! reorder_modules.containsKey(module_name))
            return null;

        IReorderModuleFactory factory = reorder_modules.get(module_name);
        return factory.construct();
    }
}