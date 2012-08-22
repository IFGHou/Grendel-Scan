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
package flex.management.runtime.messaging.services.messaging.adapters;

import flex.management.BaseControl;
import flex.management.runtime.messaging.services.ServiceAdapterControl;
import flex.messaging.services.messaging.adapters.JMSAdapter;

/**
 * The <code>JMSAdapterControl</code> class is the MBean implemenation
 * for monitoring and managing <code>JMSAdapter</code>s at runtime.
 * 
 * @author shodgson
 */
public class JMSAdapterControl extends ServiceAdapterControl implements
        JMSAdapterControlMBean
{
    private static final String TYPE = "JMSAdapter";
    private JMSAdapter jmsAdapter;
    
    /**
     * Constructs a <code>JMSAdapterControl</code>, assigning its id, managed
     * <code>JMSAdapter</code> and parent MBean.
     * 
     * @param serviceAdapter The <code>JMSAdapter</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public JMSAdapterControl(JMSAdapter serviceAdapter, BaseControl parent)
    {
        super(serviceAdapter, parent);
        jmsAdapter = serviceAdapter;
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getType()
     */
    @Override public String getType()
    {
        return TYPE;
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getTopicProducerCount()
     */
    @Override public Integer getTopicProducerCount()
    {
        return new Integer(jmsAdapter.getTopicProducerCount());
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getTopicConsumerCount()
     */
    @Override public Integer getTopicConsumerCount()
    {
        return new Integer(jmsAdapter.getTopicConsumerCount());
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getTopicConsumerIds()
     */
    @Override public String[] getTopicConsumerIds()
    {
        return jmsAdapter.getTopicConsumerIds();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getQueueProducerCount()
     */
    @Override public Integer getQueueProducerCount()
    {
        return new Integer(jmsAdapter.getQueueProducerCount());
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getQueueConsumerCount()
     */
    @Override public Integer getQueueConsumerCount()
    {        
        return new Integer(jmsAdapter.getQueueConsumerCount());
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#getQueueConsumerIds()
     */
    @Override public String[] getQueueConsumerIds()
    {
        return jmsAdapter.getQueueConsumerIds();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.JMSAdapterControlMBean#removeConsumer(java.lang.String)
     */
    @Override public void removeConsumer(String consumerId)
    {
        jmsAdapter.removeConsumer(consumerId);
    }
}
