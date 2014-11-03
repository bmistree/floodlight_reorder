package Reorder;

import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;


public interface IProtocolUtil
{
    public void clear_flow_table(
        IOFSwitch of_switch,FloodlightReorder floodlight_reorder);

    public void clear_flow_table(
        SynchronizedSwitch synced_switch, FloodlightReorder floodlight_reorder);

    public int num_entries(IOFSwitch of_switch);
    public int num_entries(SynchronizedSwitch synced_switch);
    public void issue_barrier_and_wait(
        SynchronizedSwitch synced_switch,FloodlightReorder floodlight_reorder);
    public OFFlowMod generate_add_flow_mod(long src_ethernet_addr);
    public OFFlowMod generate_rm_flow_mod(long src_ethernet_addr);
}