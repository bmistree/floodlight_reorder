package Reorder;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;

import net.floodlightcontroller.core.ImmutablePort;
import net.floodlightcontroller.core.IOFSwitch.PortChangeType;

/**
   Waits for an OFSwitch to register with floodlight.  Given this
   switch has registered, notifies anyone that's waiting on a switch
   that they can use it.
 */

public class WaitOnSwitchActivated implements IOFSwitchListener
{
    protected long of_switch_id = -1;
    protected boolean switch_id_set = false;
    
    protected final ReentrantLock set_lock = new ReentrantLock();
    protected final Condition set_condition = set_lock.newCondition();
    
    /**
       Blocks until we get a new switch from floodlight.
     */
    public long get_switch_id()
    {
        set_lock.lock();
        try
        {
            while (! switch_id_set)
            {
                try
                {
                    set_condition.await();
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                    // should never receive an interrupted exception.
                    assert(false);
                }
            }
            return of_switch_id;
        }
        finally
        {
            set_lock.unlock();
        }
    }

    /** IOFSwitchListener Overrides */
    /**
     * Fired when a switch becomes active *on the local controller*, I.e.,
     * the switch is connected to the local controller and is in MASTER mode
     * @param switchId the datapath Id of the switch
     */
    @Override
    public void switchActivated(long switch_id)
    {
        // once activated, make the switch available
        set_lock.lock();
        switch_id_set = true;
        of_switch_id = switch_id;
        set_condition.signalAll();
        set_lock.unlock();
    }

    
    /**
     * Fired when switch becomes known to the controller cluster. I.e.,
     * the switch is connected at some controller in the cluster
     * @param switch_id the datapath Id of the new switch
     */
    @Override
    public void switchAdded(long switch_id)
    {
        // do nothing, wait until swith is activated.
    }

    /**
     * Fired when a switch disconnects from the cluster ,
     * @param switch_id the datapath Id of the switch
     */
    @Override
    public void switchRemoved(long switch_id)
    {
        // Assumption is that once a switch has been added, it will be
        // permanently available.
        assert(false);
    }
    
    /**
     * Fired when a port on a known switch changes.
     *
     * A user of this notification needs to take care if the port and type
     * information is used directly and if the collection of ports has been
     * queried as well. This notification will only be dispatched after the
     * the port changes have been committed to the IOFSwitch instance. However,
     * if a user has previously called {@link IOFSwitch#getPorts()} or related
     * method a subsequent update might already be present in the information
     * returned by getPorts.
     * @param switch_id
     * @param port
     * @param type
     */
    @Override
    public void switchPortChanged(long switch_id,
                                  ImmutablePort port,
                                  IOFSwitch.PortChangeType type)
    {}


    
    /**
     * Fired when any non-port related information (e.g., attributes,
     * features) change after a switchAdded
     * TODO: currently unused
     * @param switch_id
     */
    @Override
    public void switchChanged(long switch_id){}
}

