package Reorder;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IOFMessageListener;
import org.openflow.protocol.OFMessage;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.core.FloodlightContext;


import org.openflow.protocol.OFType;

public class FloodlightReorder
    implements IFloodlightModule, IOFMessageListener
{
    protected IFloodlightProviderService floodlight_provider = null;
    protected ILinkDiscoveryService link_discovery_service = null;


    public void register_switch_listener(IOFSwitchListener switch_listener)
    {
        floodlight_provider.addOFSwitchListener(switch_listener);
    }

    /** IFloodlightModule overrides */
    @Override
    public void init(FloodlightModuleContext context)
        throws FloodlightModuleException
    {
        floodlight_provider = context.getServiceImpl(IFloodlightProviderService.class);
        link_discovery_service = context.getServiceImpl(ILinkDiscoveryService.class);
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
    }

    /** IOFMessageListener overrides */
    @Override
    public net.floodlightcontroller.core.IListener.Command receive(
        IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
    {
        /**
           FIXME: must finish implementation of this method.
         */
        assert(false);
        return null;
    }

    @Override
    public String getName()
    {
        return "FloodlightReorderer";
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