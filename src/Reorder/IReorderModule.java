package Reorder;

import net.floodlightcontroller.core.IOFSwitch;
import java.io.IOException;

public interface IReorderModule
{
    /**
       @param switch_to_send_commands_to --- The switcht that we are
       trying to cause to reorder our commands.
     */
    public void init(IOFSwitch switch_to_send_commands_to);
    /**
       @returns true if got a reordering; false if did not.
     */
    public boolean try_to_reorder();
}