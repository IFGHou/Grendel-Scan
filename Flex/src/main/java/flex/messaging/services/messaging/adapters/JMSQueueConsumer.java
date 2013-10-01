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
package flex.messaging.services.messaging.adapters;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.NamingException;

import flex.messaging.MessageException;

/**
 * A <code>JMSConsumer</code> subclass specifically for JMS Queue receivers.
 */
public class JMSQueueConsumer extends JMSConsumer
{
    //--------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    //--------------------------------------------------------------------------

    /**
     * Starts the <code>JMSQueueConsumer</code>.
     *
     * @throws NamingException The thrown naming exception.
     * @throws JMSException The thrown JMS exception.
     */
    @Override public void start() throws NamingException, JMSException
    {
        super.start();

        // Establish queue
        Queue queue = null;
        try
        {
            queue = (Queue)destination;
        }
        catch (ClassCastException cce)
        {
            // JMS queue proxy for JMS destination ''{0}'' has a destination type of ''{1}'' which is not Queue.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_QUEUE_DESTINATION, new Object[] {destinationJndiName, destination.getClass().getName()});
            throw me;
        }

        // Create connection
        try
        {
            QueueConnectionFactory queueFactory = (QueueConnectionFactory)connectionFactory;
            if (connectionCredentials != null)
                connection = queueFactory.createQueueConnection(connectionCredentials.getUsername(), connectionCredentials.getPassword());
            else
                connection = queueFactory.createQueueConnection();
        }
        catch (ClassCastException cce)
        {
            // JMS queue proxy for JMS destination ''{0}'' has a connection factory type of ''{1}'' which is not QueueConnectionFactory.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_QUEUE_FACTORY, new Object[] {destinationJndiName, connectionFactory.getClass().getName()});
            throw me;
        }

        QueueConnection queueConnection = (QueueConnection)connection;

        // Create queue session on the connection
        session = queueConnection.createQueueSession(false, getAcknowledgeMode());

        // Create receiver on the queue session
        QueueSession queueSession = (QueueSession) session;

        // Handle message selectors
        if (selectorExpression != null)
            consumer = queueSession.createReceiver(queue, selectorExpression);
        else
            consumer = queueSession.createReceiver(queue);

        startMessageReceiver();
    }
}
