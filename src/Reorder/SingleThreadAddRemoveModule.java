package Reorder;

import net.floodlightcontroller.core.IOFSwitch;


public class SingleThreadAddRemoveModule implements IReorderModule
{
    public final static String REORDER_NAME = "SingleThreadAddRemove";
    
    protected SynchronizedSwitch synced_switch = null;
    
    /**
       @returns true if got a reordering; false if did not.
     */
    @Override
    public boolean try_to_reorder()
    {
        return false;
    }
    
    @Override
    public void init(IOFSwitch switch_to_send_commands_to)
    {
        synced_switch = new SynchronizedSwitch(switch_to_send_commands_to);
    }

    @Override
    public String reorder_module_name()
    {
        return REORDER_NAME;
    }

    public enum Factory implements IReorderModuleFactory
    {
        INSTANCE;
        
        @Override
        public IReorderModule construct()
        {
            return new SingleThreadAddRemoveModule();
        }
    }
}
