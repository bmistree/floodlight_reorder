package Reorder;

import java.util.List;
import java.util.Collection;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.FloodlightModuleLoader;
import net.floodlightcontroller.core.module.IFloodlightModuleContext;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.IFloodlightProviderService;

public class ReorderRunner
{
    public final static String DEFAULT_MODULE_CONFIG_FILENAME =
        "floodlight.modules";
    protected final List<IReorderModule> reorder_module_list;

    /** Gets set in start_floodlight */
    protected FloodlightReorder floodlight_reorder_module = null;
    
    public ReorderRunner(List<IReorderModule> _reorder_module_list)
    {
        reorder_module_list = _reorder_module_list;
    }


    /**
       @returns true if got a reordering; false if did not.
     */
    public boolean try_to_reorder()
    {
        final WaitOnSwitchActivated wait_on_switch_activated =
            new WaitOnSwitchActivated();
        
        final ReorderRunner tmp_this = this;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    tmp_this.start_floodlight(wait_on_switch_activated);
                }
                catch (FloodlightModuleException ex)
                {
                    assert(false);
                }
            }
        };
        t.setDaemon(true);
        t.start();

        long switch_id = wait_on_switch_activated.get_switch_id();
        IOFSwitch of_switch = floodlight_reorder_module.get_switch(switch_id);
        return run_modules(of_switch);
    }

    /**
       @returns true if got a reordering; false if did not.
     */
    protected boolean run_modules(IOFSwitch of_switch)
    {
        boolean to_return = false;

        for (IReorderModule reorder_module : reorder_module_list)
        {
            reorder_module.init(of_switch,floodlight_reorder_module);
            boolean got_reordered = reorder_module.try_to_reorder();
            to_return = to_return || got_reordered;

            // clear previous table for next module to run.
            Util.clear_flow_table(of_switch,floodlight_reorder_module);
        }
        return to_return;
    }

    
    protected void start_floodlight(IOFSwitchListener switch_listener)
        throws FloodlightModuleException
    {
        // Load modules
        FloodlightModuleLoader floodlight_module_loader =
            new FloodlightModuleLoader();

        IFloodlightModuleContext module_context =
            floodlight_module_loader.load_modules_from_class_loader(
                DEFAULT_MODULE_CONFIG_FILENAME);

        IFloodlightProviderService controller =
            module_context.getServiceImpl(IFloodlightProviderService.class);

        Collection<IFloodlightModule> all_modules = module_context.getAllModules();
        for (IFloodlightModule floodlight_module : all_modules)
        {            
            if (floodlight_module.getClass() == FloodlightReorder.class)
            {
                floodlight_reorder_module =
                    (FloodlightReorder)floodlight_module;
                break;
            }
        }
        //// DEBUG
        if (floodlight_reorder_module == null)
            Util.force_assert("No floodlight reorder module");
        //// END DEBUG
        
        floodlight_reorder_module.add_switch_listener(switch_listener);
        controller.run();
    }    
}