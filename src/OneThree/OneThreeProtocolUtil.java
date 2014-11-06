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
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
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
    public void print_all_entries(SynchronizedSwitch synced_switch)
    {
        print_all_entries(synced_switch.of_switch);
    }
    
    @Override
    public void print_all_entries(IOFSwitch of_switch)
    {
        num_entries(of_switch, true);
    }
    
    @Override
    public int num_entries(SynchronizedSwitch synced_switch)
    {
        return num_entries(synced_switch.of_switch);
    }
    
    @Override
    public int num_entries(IOFSwitch of_switch)
    {
        return num_entries(of_switch,false);
    }
    
    protected int num_entries(IOFSwitch of_switch, boolean print_all_entries)
    {
        OFStatisticsRequest req = new OFStatisticsRequest();
        req.setStatisticsType(OFStatisticsType.FLOW);
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

            to_return = stats_reply_list.size();
            
            if (print_all_entries)
            {
                for (OFStatistics stats : stats_reply_list)
                {
                    OFFlowStatisticsReply reply =
                        (OFFlowStatisticsReply) stats;
                    System.out.println(reply.toString());
                }
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
        return generate_flow_mod(src_ethernet_addr,null,true);
    }

    @Override
    public OFFlowMod generate_rm_eth_src_flow_mod(long src_ethernet_addr)
    {
        return generate_flow_mod(src_ethernet_addr,null,false);
    }

    @Override
    public OFFlowMod generate_add_eth_src_and_tcp_src_port_flow_mod(
        long src_ethernet_addr, int src_tcp_port)
    {
        return generate_flow_mod(src_ethernet_addr,src_tcp_port,true);
    }

    @Override
    public OFFlowMod generate_rm_tcp_src_port_flow_mod(int src_tcp_port)
    {
        return generate_flow_mod(null,src_tcp_port,false);
    }

    @Override
    public OFFlowMod generate_full_flow_mod(long some_num)
    {
        Util.force_assert("FIXME: must finish writing generate_full_flow_mod");
        return null;
    }
    
    
    
    /**
       @param src_ethernet_addr --- Can be null if we want to match
       across all ethernet source addresses.

       @param src_tcp_port --- Can be null if we want to match across
       all tcp source ports.
       
       @returns a flow mod that adds or removes an instruction to
       perform an action on a particular ethernet address.
     */
    protected OFFlowMod generate_flow_mod(
        Long src_ethernet_addr, Integer src_tcp_port, boolean is_add)
    {
        OFFlowMod to_return = new OFFlowMod();
        if (is_add)
            to_return.setCommand(OFFlowMod.OFPFC_ADD);
        else
            to_return.setCommand(OFFlowMod.OFPFC_DELETE);
        
        // generate match
        OFMatch of_match =
            generate_flowmod_match(src_ethernet_addr,src_tcp_port);
        to_return.setMatch(of_match);
        
        // add operations for insertions.  empty instruction list
        // means that we should drop
        to_return.setInstructions(new ArrayList<OFInstruction>());
        return to_return;
    }

    /**
       @param src_ethernet_addr --- Can be null if we want to match
       across all ethernet source addresses.

       @param src_tcp_port --- Can be null if we want to match across
       all tcp source ports.
    */
    protected OFMatch generate_flowmod_match(
        Long src_ethernet_addr, Integer src_tcp_port)
    {
        String ofmatch_comb_str = "";

        if (src_ethernet_addr != null)
        {
            MACAddress mac_addr = MACAddress.valueOf(src_ethernet_addr);
            ofmatch_comb_str += OFOXMFieldType.ETH_SRC.getName();
            ofmatch_comb_str += "=" + mac_addr.toString();
        }

        if (src_tcp_port != null)
        {
            if (src_ethernet_addr != null)
                ofmatch_comb_str += ",";

            ofmatch_comb_str += OFOXMFieldType.ETH_TYPE.getName();
            ofmatch_comb_str += "=0x0800,";

            ofmatch_comb_str += OFOXMFieldType.IP_PROTO.getName();
            ofmatch_comb_str += "=6,";
            
            ofmatch_comb_str += OFOXMFieldType.TCP_SRC.getName();
            ofmatch_comb_str += "=" + src_tcp_port.toString();
        }

        return OFMatch.fromString(ofmatch_comb_str);
    }
}