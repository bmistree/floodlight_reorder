package Reorder;

import java.util.List;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.statistics.OFAggregateStatisticsReply;
import org.openflow.protocol.statistics.OFAggregateStatisticsRequest;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.FloodlightContext;

public class SynchronizedSwitch
{
    private final IOFSwitch of_switch;
    public SynchronizedSwitch(IOFSwitch _of_switch)
    {
        of_switch = _of_switch;
    }

    public synchronized void write (OFMessage m, FloodlightContext bc)
    {
        try
        {
            of_switch.write(m,bc);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            Util.force_assert("IOException on write.");
        }
    }

    public synchronized int flow_table_entry_size()
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
}