package Reorder;

import java.io.IOException;

import org.openflow.protocol.OFMessage;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.FloodlightContext;

public class SynchronizedSwitch
{
    private final IOFSwitch of_switch;
    public SynchronizedSwitch(IOFSwitch _of_switch)
    {
        of_switch = _of_switch;
    }

    public synchronized void write (
        OFMessage m, FloodlightContext bc) throws IOException
    {
        of_switch.write(m,bc);
    }
}