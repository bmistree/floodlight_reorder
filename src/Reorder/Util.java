package Reorder;

import java.util.ArrayList;

import net.floodlightcontroller.util.MACAddress;

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