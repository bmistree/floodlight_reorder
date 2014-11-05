package Reorder;

import java.util.List;
import java.util.ArrayList;

import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;

public class LoadTableAndPrintModule implements IReorderModule, ILoggable
{
    public final static String REORDER_NAME = "LoadTableAndPrint";
    public final static int NUM_ADDS = 500;

    
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
        Util.log_info(this,"Sending add flow mods");
        
        // apply lots of changes.
        for (OFFlowMod flow_mod : flowmod_list)
            synced_switch.write(flow_mod,null);

        Util.log_info(this,"Issuing flowmod barrier");

        // wait for changes to be applied
        protocol_util.issue_barrier_and_wait(synced_switch,floodlight_reorder);

        // dump the flow table.
        protocol_util.print_all_entries(synced_switch);

        // clear all entries
        protocol_util.clear_flow_table(
            synced_switch,floodlight_reorder);
        
        // never reordering in this module: return false;
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
            flowmod_list.add(protocol_util.generate_add_eth_src_flow_mod(i));
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
            return new LoadTableAndPrintModule();
        }
    }
}
