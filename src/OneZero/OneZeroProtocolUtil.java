package OneZero;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.core.IFloodlightProviderService;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;

import org.openflow.protocol.action.OFAction;

import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.openflow.protocol.statistics.OFAggregateStatisticsReply;
import org.openflow.protocol.statistics.OFAggregateStatisticsRequest;

import Reorder.IProtocolUtil;
import Reorder.Util;
import Reorder.SynchronizedSwitch;
import Reorder.FloodlightReorder;
import Reorder.ILoggable;

public enum OneZeroProtocolUtil implements IProtocolUtil, ILoggable
{
    INSTANCE;

    private static final AtomicInteger xid_generator =
        new AtomicInteger(10);

    @Override
    public String loggable_module_name()
    {
        return "OneZeroProtocolUtil";
    }

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
        
        // entries will never disappear on their own from flow table.
        flow_mod.setHardTimeout((short)0);
        flow_mod.setIdleTimeout((short)0);

        // this message is not a response to any packet in.
        flow_mod.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        
        // set output port: applies deletes to all output ports
        flow_mod.setOutPort(OFPort.OFPP_NONE);

        flow_mod.setXid(xid_generator.getAndIncrement());
        
        // set ofmatch
        OFMatch of_match = new OFMatch();
        of_match.fromString("");
        flow_mod.setMatch(of_match);
        
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
        req.setStatisticType(OFStatisticsType.AGGREGATE);
        int requestLength = req.getLengthU();
        
        OFAggregateStatisticsRequest specificReq =
            new OFAggregateStatisticsRequest();
        OFMatch match = new OFMatch();
        specificReq.setMatch(match);

        // match all tables
        specificReq.setTableId((byte)0xff);
        // do not filter on ports
        specificReq.setOutPort(OFPort.OFPP_NONE.getValue());
        
        req.setStatistics(
            Collections.singletonList((OFStatistics)specificReq));
        
        requestLength += specificReq.getLength();
        req.setLengthU(requestLength);

        int to_return = -1;
        try
        {
            Util.log_info(this,"Requesting number of entries");
            Future<List<OFStatistics>> future_stats_reply_list =
                of_switch.queryStatistics(req);
            List<OFStatistics> stats_reply_list =
                future_stats_reply_list.get();

            to_return = 0;

            Util.log_info(
                this,
                "Received number of entries; rep size: " +
                stats_reply_list.size());
            
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

        // entries will never disappear on their own from flow table.
        to_return.setHardTimeout((short)0);
        to_return.setIdleTimeout((short)0);

        // this message is not a response to any packet in.
        to_return.setBufferId(OFPacketOut.BUFFER_ID_NONE);

        to_return.setPriority((short)22);
        to_return.setActions(new ArrayList<OFAction>());
        
        // set output port: applies deletes to all output ports
        to_return.setOutPort(OFPort.OFPP_NONE);

        // set transaction id
        to_return.setXid(xid_generator.getAndIncrement());
        return to_return;
    }

    protected OFMatch generate_ethernet_src_match(long src_ethernet_addr)
    {
        MACAddress mac_addr = MACAddress.valueOf(src_ethernet_addr);

        OFMatch match = new OFMatch();
        match.fromString(OFMatch.STR_DL_SRC + "=" + mac_addr.toString());
        return match;
    }
}