package Reorder;

import java.util.Set;
import java.util.HashSet;

import net.floodlightcontroller.core.IOFSwitch;

public class ReorderRunner
{
    protected final Set<IReorderModule> reorder_module_set;
    protected final WaitOnSwitchActivated wait_on_switch_activated;
    protected final FloodlightReorder floodlight_reorder;
    
    public ReorderRunner(
        Set<IReorderModule> _reorder_module_set,
        WaitOnSwitchActivated _wait_on_switch_activated,
        FloodlightReorder _floodlight_reorder)
    {
        reorder_module_set = _reorder_module_set;
        wait_on_switch_activated = _wait_on_switch_activated;
        floodlight_reorder = _floodlight_reorder;
    }

    /**
       @returns true if got a reordering; false if did not.
     */
    public boolean try_to_reorder()
    {
        boolean to_return = false;
        long switch_id = wait_on_switch_activated.get_switch_id();
        IOFSwitch of_switch = floodlight_reorder.get_switch(switch_id);

        for (IReorderModule reorder_module : reorder_module_set)
        {
            reorder_module.init(of_switch);
            boolean got_reordered = reorder_module.try_to_reorder();
            to_return = to_return || got_reordered;
        }
        return to_return;
    }
}