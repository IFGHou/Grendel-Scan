/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.management.runtime.messaging.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.management.ObjectName;

import flex.management.BaseControl;
import flex.management.runtime.messaging.MessageBrokerControl;
import flex.messaging.Destination;
import flex.messaging.services.Service;

/**
 * The <code>ServiceControl</code> class is the MBean implementation for
 * monitoring and managing a <code>Service</code> at runtime.
 * 
 * @author shodgson
 */
public abstract class ServiceControl extends BaseControl implements ServiceControlMBean
{
    protected Service service;
    private List destinations;
    
    /**
     * Constructs a <code>ServiceControl</code>, assigning its id, managed service and
     * parent MBean.
     * 
     * @param service The <code>Service</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public ServiceControl(Service service, BaseControl parent)
    {
        super(parent);
        this.service = service;
        destinations = new ArrayList(); 
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override public String getId()
    {
        return service.getId();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ServiceControlMBean#isRunning()
     */
    @Override public Boolean isRunning()
    {
        return Boolean.valueOf(service.isStarted());
    }
    
    
    /**
     * Adds the <code>ObjectName</code> of a destination registered with the managed service.
     * 
     * @param value The <code>ObjectName</code> of a destination registered with the managed service.
     */
    public void addDestination(ObjectName value)
    {
        destinations.add(value);
    }
    
    /**
     * Removes the <code>ObjectName</code> of a destination registered with the managed service.
     * 
     * @param value The <code>ObjectName</code> of a destination registered with the managed service.
     */
    public void removeDestination(ObjectName value)
    {
        destinations.remove(value);
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ServiceControlMBean#getDestinations()
     */
    @Override public ObjectName[] getDestinations()
    {
        int size = destinations.size();
        ObjectName[] destinationNames = new ObjectName[size];
        for (int i = 0; i < size; ++i)
        {
            destinationNames[i] = (ObjectName)destinations.get(i);
        }
        return destinationNames;
    }
    
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ServiceControlMBean#getStartTimestamp()
     */
    @Override public Date getStartTimestamp()
    {
        return startTimestamp;
    }
    
    
    /*
     *  (non-Javadoc)
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    @Override public void preDeregister() throws Exception
    {
        MessageBrokerControl parent = (MessageBrokerControl)getParentControl();
        parent.removeService(getObjectName());
        
        // Unregister destinations of the service
        for (Iterator iter = service.getDestinations().values().iterator(); iter.hasNext();) {
            Destination child = (Destination) iter.next();
            if (child.getControl() != null)
            {
                child.getControl().unregister();
                child.setControl(null);
                child.setManaged(false);
            }
            
        }
        
        super.preDeregister();
    }
    
}
