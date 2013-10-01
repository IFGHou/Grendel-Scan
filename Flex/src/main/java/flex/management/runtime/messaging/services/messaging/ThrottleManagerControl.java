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
package flex.management.runtime.messaging.services.messaging;

import java.util.Date;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.messaging.services.messaging.ThrottleManager;

/**
 * The <code>ThrottleManagerControl</code> class is the MBean implementation for
 * monitoring and managing a <code>ThrottleManager</code> at runtime.
 * 
 * @author shodgson
 */
public class ThrottleManagerControl extends BaseControl implements
        ThrottleManagerControlMBean
{
    private ThrottleManager throttleManager;
    private long clientIncomingMessageThrottleStart;
    private int clientIncomingMessageThrottleCount;
    private Date lastClientIncomingMessageThrottleTimestamp;
    private long clientOutgoingMessageThrottleStart;
    private int clientOutgoingMessageThrottleCount;
    private Date lastClientOutgoingMessageThrottleTimestamp;
    private long destinationIncomingMessageThrottleStart;
    private int destinationIncomingMessageThrottleCount;
    private Date lastDestinationIncomingMessageThrottleTimestamp;
    private long destinationOutgoingMessageThrottleStart;
    private int destinationOutgoingMessageThrottleCount;
    private Date lastDestinationOutgoingMessageThrottleTimestamp;

    /**
     * Constructs a new <code>ThrottleManagerControl</code> instance, assigning its
     * backing <code>ThrottleManager</code>.
     * 
     * @param throttleManager The <code>ThrottleManager</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public ThrottleManagerControl(ThrottleManager throttleManager, BaseControl parent)
    {
        super(parent);
        this.throttleManager = throttleManager;
        clientIncomingMessageThrottleStart = System.currentTimeMillis();
        clientOutgoingMessageThrottleStart = clientIncomingMessageThrottleStart;
        destinationIncomingMessageThrottleStart = clientIncomingMessageThrottleStart;
        destinationOutgoingMessageThrottleStart = clientIncomingMessageThrottleStart;
    }
    
    @Override protected void onRegistrationComplete()
    {
        String name = this.getObjectName().getCanonicalName();
        String[] attributes = { 
                "ClientIncomingMessageThrottleCount", "ClientIncomingMessageThrottleFrequency",
                "ClientOutgoingMessageThrottleCount", "ClientOutgoingMessageThrottleFrequency",
                "DestinationIncomingMessageThrottleCount", "DestinationIncomingMessageThrottleFrequency",
                "DestinationOutgoingMessageThrottleCount", "DestinationOutgoingMessageThrottleFrequency",
                "LastClientIncomingMessageThrottleTimestamp", "LastClientOutgoingMessageThrottleTimestamp",
                "LastDestinationIncomingMessageThrottleTimestamp", "LastDestinationOutgoingMessageThrottleTimestamp"
        };
        
        getRegistrar().registerObjects(AdminConsoleTypes.DESTINATION_POLLABLE, name, attributes);
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override public String getId()
    {
        return throttleManager.getId();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getType()
     */
    @Override public String getType()
    {
        return ThrottleManager.TYPE;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getClientIncomingMessageThrottleCount()
     */
    @Override public Integer getClientIncomingMessageThrottleCount()
    {
        return new Integer(clientIncomingMessageThrottleCount);
    }
    
    /**
     * Increments the count of throttled incoming client messages.
     */
    public void incrementClientIncomingMessageThrottleCount()
    {
        ++clientIncomingMessageThrottleCount;
        lastClientIncomingMessageThrottleTimestamp = new Date();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#resetClientIncomingMessageThrottleCount()
     */
    @Override public void resetClientIncomingMessageThrottleCount()
    {
        clientIncomingMessageThrottleStart = System.currentTimeMillis();
        clientIncomingMessageThrottleCount = 0;
        lastClientIncomingMessageThrottleTimestamp = null;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getLastClientIncomingMessageThrottleTimestamp()
     */
    @Override public Date getLastClientIncomingMessageThrottleTimestamp()
    {
        return lastClientIncomingMessageThrottleTimestamp;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getClientIncomingMessageThrottleFrequency()
     */
    @Override public Double getClientIncomingMessageThrottleFrequency()
    {
        if (clientIncomingMessageThrottleCount > 0)
        {
            double runtime = differenceInMinutes(clientIncomingMessageThrottleStart, System.currentTimeMillis());
            return new Double(clientIncomingMessageThrottleCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getClientOutgoingMessageThrottleCount()
     */
    @Override public Integer getClientOutgoingMessageThrottleCount()
    {
        return new Integer(clientOutgoingMessageThrottleCount);
    }
    
    /**
     * Increments the count of throttled outgoing client messages.
     */
    public void incrementClientOutgoingMessageThrottleCount()
    {
        ++clientOutgoingMessageThrottleCount;
        lastClientOutgoingMessageThrottleTimestamp = new Date();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#resetClientOutgoingMessageThrottleCount()
     */
    @Override public void resetClientOutgoingMessageThrottleCount()
    {
        clientOutgoingMessageThrottleStart = System.currentTimeMillis();
        clientOutgoingMessageThrottleCount = 0;
        lastClientOutgoingMessageThrottleTimestamp = null;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getLastClientOutgoingMessageThrottleTimestamp()
     */
    @Override public Date getLastClientOutgoingMessageThrottleTimestamp()
    {
        return lastClientOutgoingMessageThrottleTimestamp;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getClientOutgoingMessageThrottleFrequency()
     */
    @Override public Double getClientOutgoingMessageThrottleFrequency()
    {
        if (clientOutgoingMessageThrottleCount > 0)
        {
            double runtime = differenceInMinutes(clientOutgoingMessageThrottleStart, System.currentTimeMillis());
            return new Double(clientOutgoingMessageThrottleCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getDestinationIncomingMessageThrottleCount()
     */
    @Override public Integer getDestinationIncomingMessageThrottleCount()
    {
        return new Integer(destinationIncomingMessageThrottleCount);
    }
    
    /**
     * Increments the count of throttled incoming destination messages.
     */
    public void incrementDestinationIncomingMessageThrottleCount()
    {
        ++destinationIncomingMessageThrottleCount;
        lastDestinationIncomingMessageThrottleTimestamp = new Date();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#resetDestinationIncomingMessageThrottleCount()
     */
    @Override public void resetDestinationIncomingMessageThrottleCount()
    {
        destinationIncomingMessageThrottleStart = System.currentTimeMillis();
        destinationIncomingMessageThrottleCount = 0;
        lastDestinationIncomingMessageThrottleTimestamp = null;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getLastDestinationIncomingMessageThrottleTimestamp()
     */
    @Override public Date getLastDestinationIncomingMessageThrottleTimestamp()
    {
        return lastDestinationIncomingMessageThrottleTimestamp;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getDestinationIncomingMessageThrottleFrequency()
     */
    @Override public Double getDestinationIncomingMessageThrottleFrequency()
    {
        if (destinationIncomingMessageThrottleCount > 0)
        {
            double runtime = differenceInMinutes(destinationIncomingMessageThrottleStart, System.currentTimeMillis());
            return new Double(destinationIncomingMessageThrottleCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getDestinationOutgoingMessageThrottleCount()
     */
    @Override public Integer getDestinationOutgoingMessageThrottleCount()
    {
        return new Integer(destinationOutgoingMessageThrottleCount);
    }
    
    /**
     * Increments the count of throttled outgoing destination messages.
     */
    public void incrementDestinationOutgoingMessageThrottleCount()
    {
        ++destinationOutgoingMessageThrottleCount;
        lastDestinationOutgoingMessageThrottleTimestamp = new Date();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#resetDestinationOutgoingMessageThrottleCount()
     */
    @Override public void resetDestinationOutgoingMessageThrottleCount()
    {
        destinationOutgoingMessageThrottleStart = System.currentTimeMillis();
        destinationOutgoingMessageThrottleCount = 0;
        lastDestinationOutgoingMessageThrottleTimestamp = null;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ThrottleManagerControlMBean#getLastDestinationOutgoingMessageThrottleTimestamp()
     */
    @Override public Date getLastDestinationOutgoingMessageThrottleTimestamp()
    {
        return lastDestinationOutgoingMessageThrottleTimestamp;
    }
    
    @Override public Double getDestinationOutgoingMessageThrottleFrequency()
    {
        if (destinationOutgoingMessageThrottleCount > 0)
        {
            double runtime = differenceInMinutes(destinationOutgoingMessageThrottleStart, System.currentTimeMillis());
            return new Double(destinationOutgoingMessageThrottleCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }
}
