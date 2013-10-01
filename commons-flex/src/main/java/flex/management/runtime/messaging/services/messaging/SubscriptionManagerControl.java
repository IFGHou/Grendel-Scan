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
package flex.management.runtime.messaging.services.messaging;

import java.util.Set;

import flex.management.BaseControl;
import flex.messaging.MessageClient;
import flex.messaging.services.messaging.SubscriptionManager;

/**
 * The <code>SubscriptionManagerControl</code> class is the MBean implementation for monitoring and managing a <code>SubscriptionManager</code> at runtime.
 * 
 * @author shodgson
 */
public class SubscriptionManagerControl extends BaseControl implements SubscriptionManagerControlMBean
{
    private SubscriptionManager subscriptionManager;

    /**
     * Constructs a new <code>SubscriptionManagerControl</code> instance, assigning its backing <code>SubscriptionManager</code>.
     * 
     * @param subscriptionManager
     *            The <code>SubscriptionManager</code> managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public SubscriptionManagerControl(SubscriptionManager subscriptionManager, BaseControl parent)
    {
        super(parent);
        this.subscriptionManager = subscriptionManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override
    public String getId()
    {
        return subscriptionManager.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControlMBean#getType()
     */
    @Override
    public String getType()
    {
        return SubscriptionManager.TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.SubscriptionManagerControlMBean#getSubscriberCount()
     */
    @Override
    public Integer getSubscriberCount()
    {
        Set subscriberIds = subscriptionManager.getSubscriberIds();
        if (subscriberIds != null)
        {
            return new Integer(subscriberIds.size());
        }
        else
        {
            return new Integer(0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.SubscriptionManagerControlMBean#getSubscriberIds()
     */
    @Override
    public String[] getSubscriberIds()
    {
        Set subscriberIds = subscriptionManager.getSubscriberIds();
        if (subscriberIds != null)
        {
            String[] ids = new String[subscriberIds.size()];
            return (String[]) subscriberIds.toArray(ids);
        }
        else
        {
            return new String[0];
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.SubscriptionManagerControlMBean#removeSubscriber(java.lang.String)
     */
    @Override
    public void removeSubscriber(String subscriberId)
    {
        MessageClient subscriber = subscriptionManager.getSubscriber(subscriberId);
        if (subscriber != null)
        {
            subscriptionManager.removeSubscriber(subscriber);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.SubscriptionManagerControlMBean#removeAllSubscribers()
     */
    @Override
    public void removeAllSubscribers()
    {
        String[] subscriberIds = getSubscriberIds();
        int length = subscriberIds.length;
        for (int i = 0; i < length; ++i)
        {
            removeSubscriber(subscriberIds[i]);
        }
    }

}
