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
package flex.management.runtime.messaging.services.messaging.adapters;

import java.io.IOException;

import flex.management.runtime.messaging.services.ServiceAdapterControlMBean;

/**
 * Defines the runtime monitoring and management interface for managed JMS adapters.
 * 
 * @author shodgson
 */
public interface JMSAdapterControlMBean extends ServiceAdapterControlMBean
{
    /**
     * Returns the number of topic producers for the adapter.
     * 
     * @return The number of topic producers for the adapter.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getTopicProducerCount() throws IOException;

    /**
     * Returns the number of topic consumers for the adapter.
     * 
     * @return The number of topic consumers for the adapter.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getTopicConsumerCount() throws IOException;

    /**
     * Returns the ids of all topic consumers.
     * 
     * @return The ids of all topic consumers.
     * @throws IOException
     *             Throws IOException.
     */
    String[] getTopicConsumerIds() throws IOException;

    /**
     * Returns the number of queue producers for the adapter.
     * 
     * @return The number of queue producers for the adapter.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getQueueProducerCount() throws IOException;

    /**
     * Returns the number of queue consumers for the adapter.
     * 
     * @return The number of queue consumers for the adapter.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getQueueConsumerCount() throws IOException;

    /**
     * Returns the ids of all queue consumers.
     * 
     * @return The ids of all queue consumers.
     * @throws IOException
     *             Throws IOException.
     */
    String[] getQueueConsumerIds() throws IOException;

    /**
     * Unsubscribes the consumer (for either a topic or queue).
     * 
     * @param consumerId
     *            The id of the consumer to unsubscribe.
     * @throws IOException
     *             Throws IOException.
     */
    void removeConsumer(String consumerId) throws IOException;
}
