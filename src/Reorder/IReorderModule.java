package Reorder;

import java.io.IOException;

import net.floodlightcontroller.core.IOFSwitch;


public interface IReorderModule
{
    /**
       @param switch_to_send_commands_to --- The switch that we are
       trying to cause to reorder our commands.
     */
    public void init(
        IProtocolUtil protocol_util,
        IOFSwitch switch_to_send_commands_to,
        FloodlightReorder floodlight_reorder);
    /**
       @returns true if got a reordering; false if did not.
     */
    public boolean try_to_reorder();

    /**
       @returns name of module.
     */
    public String reorder_module_name();
}