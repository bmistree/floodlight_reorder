package Reorder;

import java.util.ArrayList;

import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.OFOXMFieldType;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFFlowMod;


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
    
    public static void clear_flow_table(IOFSwitch of_switch)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        clear_flow_table(synced_switch);
    }

    public static void clear_flow_table(SynchronizedSwitch synced_switch)
    {
        // generate a flow mod to clear full table
        OFFlowMod flow_mod = new OFFlowMod();
        flow_mod.setCommand(OFFlowMod.OFPFC_DELETE);
        
        // actually clear it.
        synced_switch.write(flow_mod,null);

        // wait until has been cleared.
        wait_on_barrier(synced_switch);
    }
    

    public static int num_entries(IOFSwitch of_switch)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        return num_entries(synced_switch);
    }

    public static int num_entries(SynchronizedSwitch synced_switch)
    {
        log_warn("Still need to collect number entries.");
        return 0;
    }
    
    public static void wait_on_barrier(IOFSwitch of_switch)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        wait_on_barrier(synced_switch);
    }
    public static void wait_on_barrier(SynchronizedSwitch synced_switch)
    {
        // FIXME: should use proper barrier here.
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
            force_assert(
                "Received interrupted exception for clear_flow_table");
        }        
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