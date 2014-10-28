package Reorder;

import java.util.Set;
import java.util.HashSet;
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
    
    protected final Set<IReorderModule> reorder_module_set;
    protected final WaitOnSwitchActivated wait_on_switch_activated;

    /** Gets set in start_floodlight */
    protected FloodlightReorder floodlight_reorder_module = null;
    
    public ReorderRunner(
        Set<IReorderModule> _reorder_module_set,
        WaitOnSwitchActivated _wait_on_switch_activated)
    {
        reorder_module_set = _reorder_module_set;
        wait_on_switch_activated = _wait_on_switch_activated;

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
    }
    
    public void start_floodlight(IOFSwitchListener switch_listener)
        throws FloodlightModuleException
    {
        // Load modules
        FloodlightModuleLoader floodlight_module_loader =
            new FloodlightModuleLoader();

        IFloodlightModuleContext module_context =
            floodlight_module_loader.loadModulesFromConfig(
                DEFAULT_MODULE_CONFIG_FILENAME);

        IFloodlightProviderService controller =
            module_context.getServiceImpl(IFloodlightProviderService.class);

        Collection<IFloodlightModule> all_modules = module_context.getAllModules();
        for (IFloodlightModule floodlight_module : all_modules)
        {
            if (floodlight_module.getClass() == FloodlightReorder.class)
            // if (floodlight_module.getName().equals(
            //         FloodlightReorder.FLOODLIGHT_REORDER_NAME))
            {
                floodlight_reorder_module =
                    (FloodlightReorder)floodlight_module;
                break;
            }
        }
        //// DEBUG
        if (floodlight_reorder_module == null)
            assert(false);
        //// END DEBUG
        
        floodlight_reorder_module.add_switch_listener(switch_listener);
        controller.run();
    }

    
    /**
       @returns true if got a reordering; false if did not.
     */
    public boolean try_to_reorder()
    {
        boolean to_return = false;
        long switch_id = wait_on_switch_activated.get_switch_id();
        IOFSwitch of_switch = floodlight_reorder_module.get_switch(switch_id);

        for (IReorderModule reorder_module : reorder_module_set)
        {
            reorder_module.init(of_switch);
            boolean got_reordered = reorder_module.try_to_reorder();
            to_return = to_return || got_reordered;
        }
        return to_return;
    }
}