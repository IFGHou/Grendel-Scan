/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import flex.messaging.MessageClient;
import flex.messaging.config.ConfigMap;
import flex.messaging.messages.Message;

/**
 * The base FlexClientOutboundQueueProcessor implementation used if a custom implementation is not specified. Its behavior is very simple. It adds all new messages in order to the tail of the outbound
 * queue and flushes all queued messages to the network as quickly as possible.
 * 
 * @author shodgson
 */
public class FlexClientOutboundQueueProcessor
{
    // --------------------------------------------------------------------------
    //
    // Variables
    //
    // --------------------------------------------------------------------------

    /**
     * The associated FlexClient.
     */
    private FlexClient client;

    /**
     * The associated endpoint's Id.
     */
    private String endpointId;

    // --------------------------------------------------------------------------
    //
    // Public Methods
    //
    // --------------------------------------------------------------------------

    /**
     * @exclude Stores the Id for the outbound queue's endpoint.
     * 
     * @param value
     *            The Id for the outbound queue's endpoint.
     */
    public void setEndpointId(String value)
    {
        endpointId = value;
    }

    /**
     * Returns the Id for the outbound queue's endpoint.
     * 
     * @return The Id for the outbound queue's endpoint.
     */
    public String getEndpointId()
    {
        return endpointId;
    }

    /**
     * @exclude Sets the associated FlexClient.
     * 
     * @param value
     *            The associated FlexClient.
     */
    public void setFlexClient(FlexClient value)
    {
        client = value;
    }

    /**
     * Returns the associated FlexClient.
     * 
     * @return The associated FlexClient.
     */
    public FlexClient getFlexClient()
    {
        return client;
    }

    /**
     * No-op; this default implementation doesn't require custom initialization. Subclasses may override to process any custom initialization properties that have been defined in the server
     * configuration.
     * 
     * @param properties
     *            A ConfigMap containing any custom initialization properties.
     */
    public void initialize(ConfigMap properties)
    {
    }

    /**
     * Always adds a new message to the tail of the queue.
     * 
     * @param outboundQueue
     *            The queue of outbound messages.
     * @param message
     *            The new message to add to the queue.
     */
    public void add(List outboundQueue, Message message)
    {
        outboundQueue.add(message);
    }

    /**
     * Always empties the queue and returns all messages to be sent to the client.
     * 
     * @param outboundQueue
     *            The queue of outbound messages.
     * @return A FlushResult containing the messages that have been removed from the outbound queue to be written to the network and a wait time for the next flush of the outbound queue that is the
     *         default for the underlying Channel/Endpoint.
     */
    public FlushResult flush(List outboundQueue)
    {
        FlushResult flushResult = new FlushResult();
        ArrayList messagesToFlush = new ArrayList();
        for (Iterator iter = outboundQueue.iterator(); iter.hasNext();)
        {
            Message message = (Message) iter.next();
            if (!isMessageExpired(message))
                messagesToFlush.add(message);
        }
        flushResult.setMessages(messagesToFlush);
        outboundQueue.clear();
        return flushResult;
    }

    /**
     * Removes all messages in the queue targeted to this specific MessageClient subscription(s) and returns them to be sent to the client. Overrides should be careful to only return messages for the
     * specified MessageClient.
     * 
     * @param client
     *            The specific MessageClient to return messages for.
     * @param outboundQueue
     *            The queue of outbound messages.
     * @return A FlushResult containing the messages that have been removed from the outbound queue to be written to the network for this MessageClient.
     */
    public FlushResult flush(MessageClient client, List outboundQueue)
    {
        FlushResult flushResult = new FlushResult();
        List messagesForClient = new ArrayList();
        Message message = null;
        for (Iterator iter = outboundQueue.iterator(); iter.hasNext();)
        {
            message = (Message) iter.next();
            if (message.getClientId().equals(client.getClientId()))
            {
                iter.remove();
                if (!isMessageExpired(message))
                    messagesForClient.add(message);
            }
        }
        flushResult.setMessages(messagesForClient);
        return flushResult;
    }

    /**
     * Utility method to test whether a message has expired or not. Messages with a timeToLive value that is shorter than the timespan from the message's timestamp up to the current system time will
     * cause this method to return true. If there are expired messages in the outbound queue, flush implementations should use this helper method to only process and return messages that have not yet
     * expired.
     * 
     * @param message
     *            The message to test for expiration.
     * 
     * @return true if the message has a timeToLive value that has expired; otherwise false.
     */
    public boolean isMessageExpired(Message message)
    {
        if (message.getTimeToLive() > 0 && (System.currentTimeMillis() - message.getTimestamp()) >= message.getTimeToLive())
            return true;
        else
            return false;
    }
}
