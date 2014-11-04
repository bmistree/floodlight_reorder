package OneThree;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.util.MACAddress;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFOXMFieldType;

import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.openflow.protocol.statistics.OFAggregateStatisticsReply;
import org.openflow.protocol.statistics.OFAggregateStatisticsRequest;

import org.openflow.protocol.instruction.OFInstruction;



import Reorder.IProtocolUtil;
import Reorder.Util;
import Reorder.SynchronizedSwitch;
import Reorder.FloodlightReorder;

public enum OneThreeProtocolUtil implements IProtocolUtil
{
    INSTANCE;

    @Override
    public void clear_flow_table(
        IOFSwitch of_switch,FloodlightReorder floodlight_reorder)
    {
        SynchronizedSwitch synced_switch = new SynchronizedSwitch(of_switch);
        clear_flow_table(synced_switch,floodlight_reorder);
    }
    
    @Override
    public void clear_flow_table(
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
    
    @Override
    public int num_entries(SynchronizedSwitch synced_switch)
    {
        return num_entries(synced_switch.of_switch);
    }
    
    @Override
    public int num_entries(IOFSwitch of_switch)
    {
        OFStatisticsRequest req = new OFStatisticsRequest();
        req.setStatisticsType(OFStatisticsType.AGGREGATE);
        int requestLength = req.getLengthU();
        
        OFAggregateStatisticsRequest specificReq = new OFAggregateStatisticsRequest();
        OFMatch match = new OFMatch();
        specificReq.setMatch(match);
        specificReq.setOutPort(OFPort.OFPP_ANY.getValue());
        specificReq.setTableId((byte) 0xff);
        req.setStatistics(specificReq);
        requestLength += specificReq.getLength();
        req.setLengthU(requestLength);

        int to_return = -1;
        try
        {
            Future<List<OFStatistics>> future_stats_reply_list =
                of_switch.queryStatistics(req);
            List<OFStatistics> stats_reply_list =
                future_stats_reply_list.get();

            to_return = 0;
            
            for (OFStatistics stats : stats_reply_list)
            {
                OFAggregateStatisticsReply reply =
                    (OFAggregateStatisticsReply) stats;
                to_return += reply.getFlowCount();
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            Util.force_assert("Got an IOException");
        }
        catch(ExecutionException ex)
        {
            ex.printStackTrace();
            Util.force_assert("Got an ExecutionException");
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();
            Util.force_assert("Got an interrupted exception");
        }
        return to_return;
    }

    @Override
    public void issue_barrier_and_wait(
        SynchronizedSwitch synced_switch,FloodlightReorder floodlight_reorder)
    {
        OFMessage barrier_request =
            floodlight_reorder.floodlight_provider.getOFMessageFactory().getMessage(
                OFType.BARRIER_REQUEST);
        synced_switch.write(barrier_request,null);
        floodlight_reorder.floodlight_mvar.barrier_finished.blocking_get();
    }

    @Override
    public OFFlowMod generate_add_eth_src_flow_mod(long src_ethernet_addr)
    {
        return generate_flow_mod(src_ethernet_addr,true);
    }

    @Override
    public OFFlowMod generate_rm_eth_src_flow_mod(long src_ethernet_addr)
    {
        return generate_flow_mod(src_ethernet_addr,false);
    }

    /**
       @returns a flow mod that adds or removes an instruction to
       perform an action on a particular ethernet address.
     */
    protected OFFlowMod generate_flow_mod(
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
    
    protected OFMatch generate_ethernet_src_match(long src_ethernet_addr)
    {
        MACAddress mac_addr = MACAddress.valueOf(src_ethernet_addr);
        String ofmatch_comb_str = OFOXMFieldType.ETH_SRC.getName();
        ofmatch_comb_str += "=" + mac_addr.toString();
        return OFMatch.fromString(ofmatch_comb_str);
    }
}