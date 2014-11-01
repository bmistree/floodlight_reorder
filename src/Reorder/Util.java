package Reorder;

import java.util.List;
import java.util.ArrayList;

import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.OFOXMFieldType;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;

public class Util
{
    public static void force_assert(String reason)
    {
        System.err.println(reason);
        assert(false);
        System.exit(-1);
    }
    
    public static void log_warn(String message)
    {
        System.out.println(message);
    }

    public static void clear_flow_table(
        IOFSwitch of_switch,FloodlightReorder floodlight_reorder)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        clear_flow_table(synced_switch,floodlight_reorder);
    }

    public static void clear_flow_table(
        SynchronizedSwitch synced_switch, FloodlightReorder floodlight_reorder)
    {
        // generate a flow mod to clear full table
        OFFlowMod flow_mod = new OFFlowMod();
        flow_mod.setCommand(OFFlowMod.OFPFC_DELETE);
        
        // actually clear it.
        synced_switch.write(flow_mod,null);

        // wait until has been cleared.
        issue_barrier_and_wait(synced_switch,floodlight_reorder);
    }

    public static int num_entries(IOFSwitch of_switch)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        return synced_switch.flow_table_entry_size();
    }

    public static void issue_barrier_and_wait(
        SynchronizedSwitch synced_switch,FloodlightReorder floodlight_reorder)
    {
        OFMessage barrier_request =
            floodlight_reorder.floodlight_provider.getOFMessageFactory().getMessage(
                OFType.BARRIER_REQUEST);
        synced_switch.write(barrier_request,null);
        floodlight_reorder.floodlight_mvar.barrier_finished.blocking_get();
    }
    
    public static OFFlowMod generate_add_flow_mod(long src_ethernet_addr)
    {
        return generate_flow_mod(src_ethernet_addr,true);
    }

    public static OFFlowMod generate_rm_flow_mod(long src_ethernet_addr)
    {
        return generate_flow_mod(src_ethernet_addr,false);
    }

    /**
       @returns a flow mod that adds or removes an instruction to
       perform an action on a particular ethernet address.
     */
    protected static OFFlowMod generate_flow_mod(
        long src_ethernet_addr, boolean is_add)
    {
        OFFlowMod to_return = new OFFlowMod();
        if (is_add)
            to_return.setCommand(OFFlowMod.OFPFC_ADD);
        else
            to_return.setCommand(OFFlowMod.OFPFC_DELETE_STRICT);
        
        // generate match
        OFMatch of_match = generate_ethernet_src_match(src_ethernet_addr);
        to_return.setMatch(of_match);
        
        // add operations for insertions.  empty instruction list
        // means that we should drop
        to_return.setInstructions(new ArrayList<OFInstruction>());
        return to_return;
    }
    
    protected static OFMatch generate_ethernet_src_match(
        long src_ethernet_addr)
    {
        MACAddress mac_addr = MACAddress.valueOf(src_ethernet_addr);
        String ofmatch_comb_str = OFOXMFieldType.ETH_SRC.getName();
        ofmatch_comb_str += "=" + mac_addr.toString();
        return OFMatch.fromString(ofmatch_comb_str);
    }
}