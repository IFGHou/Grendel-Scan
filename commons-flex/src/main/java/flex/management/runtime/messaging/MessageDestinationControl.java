/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.management.runtime.messaging;

import java.util.Date;

import javax.management.ObjectName;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.messaging.Destination;

/**
 * The <code>MessageDestinationControl</code> class is the MBean implementation for monitoring and managing a <code>MessageDestination</code> at runtime.
 * 
 * @author shodgson
 */
public class MessageDestinationControl extends DestinationControl implements MessageDestinationControlMBean
{
    private static final String TYPE = "MessageDestination";
    private ObjectName messageCache;
    private ObjectName throttleManager;
    private ObjectName subscriptionManager;

    private int serviceMessageCount = 0;
    private Date lastServiceMessageTimestamp;
    private long serviceMessageStart;
    private int serviceCommandCount = 0;
    private Date lastServiceCommandTimestamp;
    private long serviceCommandStart;
    private int serviceMessageFromAdapterCount = 0;
    private Date lastServiceMessageFromAdapterTimestamp;
    private long serviceMessageFromAdapterStart;

    /**
     * Constructs a new <code>MessageDestinationControl</code> instance.
     * 
     * @param destination
     *            The destination managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public MessageDestinationControl(Destination destination, BaseControl parent)
    {
        super(destination, parent);
        serviceMessageStart = System.currentTimeMillis();
        serviceCommandStart = serviceMessageStart;
        serviceMessageFromAdapterStart = serviceMessageStart;
    }

    @Override
    protected void onRegistrationComplete()
    {
        String name = this.getObjectName().getCanonicalName();

        String[] pollablePerInterval = { "ServiceCommandCount", "ServiceMessageCount", "ServiceMessageFromAdapterCount" };
        String[] pollableGeneral = { "ServiceCommandFrequency", "ServiceMessageFrequency", "ServiceMessageFromAdapterFrequency", "LastServiceCommandTimestamp", "LastServiceMessageTimestamp", "LastServiceMessageFromAdapterTimestamp" };

        getRegistrar().registerObjects(new int[] { AdminConsoleTypes.DESTINATION_POLLABLE, AdminConsoleTypes.GRAPH_BY_POLL_INTERVAL }, name, pollablePerInterval);
        getRegistrar().registerObjects(AdminConsoleTypes.DESTINATION_POLLABLE, name, pollableGeneral);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControlMBean#getType()
     */
    @Override
    public String getType()
    {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.MessageDestinationControlMBean#getMessageCache()
     */
    @Override
    public ObjectName getMessageCache()
    {
        return messageCache;
    }

    /**
     * Sets the <code>ObjectName</code> for the message cache used by the managed destination.
     * 
     * @param value
     *            The <code>ObjectName</code> for the message cache.
     */
    public void setMessageCache(ObjectName value)
    {
        messageCache = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.MessageDestinationControlMBean#getThrottleManager()
     */
    @Override
    public ObjectName getThrottleManager()
    {
        return throttleManager;
    }

    /**
     * Sets the <code>ObjectName</code> for the throttle manager used by the managed destination.
     * 
     * @param value
     *            The <code>ObjectName</code> for the throttle manager.
     */
    public void setThrottleManager(ObjectName value)
    {
        throttleManager = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.MessageDestinationControlMBean#getSubscriptionManager()
     */
    @Override
    public ObjectName getSubscriptionManager()
    {
        return subscriptionManager;
    }

    /**
     * Sets the <code>ObjectName</code> for the subscription manager used by the managed destination.
     * 
     * @param value
     *            The <code>ObjectName</code> for the subscription manager.
     */
    public void setSubscriptionManager(ObjectName value)
    {
        subscriptionManager = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceMessageCount()
     */
    @Override
    public Integer getServiceMessageCount()
    {
        return new Integer(serviceMessageCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#resetServiceMessageCount()
     */
    @Override
    public void resetServiceMessageCount()
    {
        serviceMessageStart = System.currentTimeMillis();
        serviceMessageCount = 0;
        lastServiceMessageTimestamp = null;
    }

    /**
     * Increments the count of messages serviced.
     */
    public void incrementServiceMessageCount()
    {
        ++serviceMessageCount;
        lastServiceMessageTimestamp = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getLastServiceMessageTimestamp()
     */
    @Override
    public Date getLastServiceMessageTimestamp()
    {
        return lastServiceMessageTimestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceMessageFrequency()
     */
    @Override
    public Double getServiceMessageFrequency()
    {
        if (serviceMessageCount > 0)
        {
            double runtime = differenceInMinutes(serviceMessageStart, System.currentTimeMillis());
            return new Double(serviceMessageCount / runtime);
        }
        else
        {
            return new Double(0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceCommandCount()
     */
    @Override
    public Integer getServiceCommandCount()
    {
        return new Integer(serviceCommandCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#resetServiceCommandCount()
     */
    @Override
    public void resetServiceCommandCount()
    {
        serviceCommandStart = System.currentTimeMillis();
        serviceCommandCount = 0;
        lastServiceCommandTimestamp = null;
    }

    /**
     * Increments the count of command messages serviced.
     */
    public void incrementServiceCommandCount()
    {
        ++serviceCommandCount;
        lastServiceCommandTimestamp = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getLastServiceCommandTimestamp()
     */
    @Override
    public Date getLastServiceCommandTimestamp()
    {
        return lastServiceCommandTimestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceCommandFrequency()
     */
    @Override
    public Double getServiceCommandFrequency()
    {
        if (serviceCommandCount > 0)
        {
            double runtime = differenceInMinutes(serviceCommandStart, System.currentTimeMillis());
            return new Double(serviceCommandCount / runtime);
        }
        else
        {
            return new Double(0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceMessageFromAdapterCount()
     */
    @Override
    public Integer getServiceMessageFromAdapterCount()
    {
        return new Integer(serviceMessageFromAdapterCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#resetServiceMessageFromAdapterCount()
     */
    @Override
    public void resetServiceMessageFromAdapterCount()
    {
        serviceMessageFromAdapterStart = System.currentTimeMillis();
        serviceMessageFromAdapterCount = 0;
        lastServiceMessageFromAdapterTimestamp = null;
    }

    /**
     * Increments the count of messages from adapters processed.
     */
    public void incrementServiceMessageFromAdapterCount()
    {
        ++serviceMessageFromAdapterCount;
        lastServiceMessageFromAdapterTimestamp = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getLastServiceMessageFromAdapterTimestamp()
     */
    @Override
    public Date getLastServiceMessageFromAdapterTimestamp()
    {
        return lastServiceMessageFromAdapterTimestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.MessageDestinationControlMBean#getServiceMessageFromAdapterFrequency()
     */
    @Override
    public Double getServiceMessageFromAdapterFrequency()
    {
        if (serviceMessageFromAdapterCount > 0)
        {
            double runtime = differenceInMinutes(serviceMessageFromAdapterStart, System.currentTimeMillis());
            return new Double(serviceMessageFromAdapterCount / runtime);
        }
        else
        {
            return new Double(0);
        }
    }
}
