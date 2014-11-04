package Reorder;

import java.util.List;
import java.util.ArrayList;

import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;

public class SingleThreadAddRemoveModule implements IReorderModule, ILoggable
{
    public final static String REORDER_NAME = "SingleThreadAddRemove";
    public final static int NUM_ADDS = 2500;

    
    protected SynchronizedSwitch synced_switch = null;
    protected FloodlightReorder floodlight_reorder = null;
    protected IProtocolUtil protocol_util = null;
    
    protected final List<OFFlowMod> flowmod_list = new ArrayList<OFFlowMod>();

    @Override
    public String loggable_module_name()
    {
        return REORDER_NAME;
    }
    
    
    /**
       @returns true if got a reordering; false if did not.
     */
    @Override
    public boolean try_to_reorder()
    {
        Util.log_info(this,"Sending flowmods");
        
        // apply lots of changes.
        for (OFFlowMod flow_mod : flowmod_list)
            synced_switch.write(flow_mod,null);

        Util.log_info(this,"Issuing flowmod barrier");
        
        // wait for changes to be applied
        protocol_util.issue_barrier_and_wait(synced_switch,floodlight_reorder);
        
        // if no reorderings, should not have any entries in switch.
        int num_entries = protocol_util.num_entries(synced_switch);
        Util.log_info(this,"Num entries: " + num_entries);
        if (num_entries != 0)
            return true;
        return false;
    }
    
    @Override
    public void init(
        IProtocolUtil _protocol_util,
        IOFSwitch switch_to_send_commands_to,
        FloodlightReorder _floodlight_reorder)
    {
        protocol_util = _protocol_util;
        synced_switch = new SynchronizedSwitch(switch_to_send_commands_to);
        floodlight_reorder = _floodlight_reorder;

        for (int i =0; i < NUM_ADDS; ++i)
        {
            // flow mod add,
            flowmod_list.add(protocol_util.generate_add_flow_mod(i));
            // flow mod remove
            flowmod_list.add(protocol_util.generate_rm_flow_mod(i));
        }
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
