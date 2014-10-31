package Reorder;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.core.FloodlightContext;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;

public class FloodlightReorder
    implements IFloodlightModule, IOFMessageListener
{
    public static String FLOODLIGHT_REORDER_NAME = "FloodlightReorderer";

    
    protected IFloodlightProviderService floodlight_provider = null;
    protected ILinkDiscoveryService link_discovery_service = null;

    protected final Set<IOFSwitchListener> switch_listeners_to_register =
        new HashSet<IOFSwitchListener>();
    protected boolean has_initialized = false;

    
    public synchronized void add_switch_listener(
        IOFSwitchListener switch_listener)
    {
        if (! has_initialized)
        {
            switch_listeners_to_register.add(switch_listener);
            return;
        }
        floodlight_provider.addOFSwitchListener(switch_listener);
    }
    
    /**
       @returns null if switch with switch_id does not exist.
     */
    public IOFSwitch get_switch(long switch_id)
    {
        return floodlight_provider.getSwitch(switch_id);
    }
    
    /** IFloodlightModule overrides */
    @Override
    public synchronized void init(FloodlightModuleContext context)
        throws FloodlightModuleException
    {
        floodlight_provider = context.getServiceImpl(IFloodlightProviderService.class);
        link_discovery_service = context.getServiceImpl(ILinkDiscoveryService.class);

        for (IOFSwitchListener switch_listener : switch_listeners_to_register)
            floodlight_provider.addOFSwitchListener(switch_listener);
        has_initialized = true;
    }
    

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies()
    {
        Collection<Class<? extends IFloodlightService>> l =
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(ILinkDiscoveryService.class);
        return l;
    }


    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices()
    {
        // this module does not provide any services, so return empty list.
        return new ArrayList<Class<? extends IFloodlightService>>();
    }

    
    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls()
    {
        // This module does not provide any services, therefore, have
        // no implementations to return.
        Map<Class<? extends IFloodlightService>, IFloodlightService> m =
            new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        return m;
    }
    
    @Override
    public void startUp(FloodlightModuleContext context)
    {
        floodlight_provider.addOFMessageListener(OFType.BARRIER_REPLY, this);
        floodlight_provider.addOFMessageListener(OFType.ERROR, this);
        floodlight_provider.addOFMessageListener(OFType.STATS_REPLY, this);
    }

    /** IOFMessageListener overrides */
    @Override
    public net.floodlightcontroller.core.IListener.Command receive(
        IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
    {
        if (msg.getType() == OFType.BARRIER_REPLY)
        {
            Util.force_assert(
                "FIXME: must still fill in barrier_reply handler");
        }
        else if (msg.getType() == OFType.ERROR)
        {
            Util.force_assert(
                "FIXME: must still fill in error_reply handler");
        }
        else if (msg.getType() == OFType.STATS_REPLY)
        {
            Util.force_assert(
                "FIXME: must still fill in stats_reply handler");
        }

        Util.force_assert(
            "Unknown received message type in floodlight reorder");
        return null;
    }

    @Override
    public String getName()
    {
        return FLOODLIGHT_REORDER_NAME;
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name)
    {
        return false;
    }
    
    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name)
    {
        return false;
    }
}