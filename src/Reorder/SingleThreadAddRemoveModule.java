package Reorder;

import java.util.List;
import java.util.ArrayList;

import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;

public class SingleThreadAddRemoveModule implements IReorderModule
{
    public final static String REORDER_NAME = "SingleThreadAddRemove";
    public final static int NUM_ADDS = 10000;
    
    protected SynchronizedSwitch synced_switch = null;
    protected final List<OFFlowMod> flowmod_list;

    public SingleThreadAddRemoveModule()
    {
        flowmod_list = new ArrayList<OFFlowMod>();
        for (int i =0; i < NUM_ADDS; ++i)
        {
            // flow mod add,
            flowmod_list.add(Util.generate_add_flow_mod(i));
            // flow mod remove
            flowmod_list.add(Util.generate_rm_flow_mod(i));
        }
    }
    
    /**
       @returns true if got a reordering; false if did not.
     */
    @Override
    public boolean try_to_reorder()
    {
        // apply lots of changes.
        for (OFFlowMod flow_mod : flowmod_list)
            synced_switch.write(flow_mod,null);

        // wait for changes to be applied
        Util.wait_on_barrier(synced_switch);

        // if no reorderings, should not have any entries in switch.
        int num_entries = Util.num_entries(synced_switch);
        if (num_entries != 0)
            return true;
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