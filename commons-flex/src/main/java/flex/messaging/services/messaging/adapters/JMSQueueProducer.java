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
package flex.messaging.services.messaging.adapters;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import flex.messaging.MessageException;

/**
 * A <code>JMSProducer</code> subclass specifically for JMS Queue senders.
 * 
 * @exclude
 */
public class JMSQueueProducer extends JMSProducer
{
    /* JMS related variables */
    private QueueSender sender;

    /**
     * Starts <code>JMSQueueProducer</code>.
     */
    @Override
    public void start() throws NamingException, JMSException
    {
        super.start();

        // Establish queue
        Queue queue = null;
        try
        {
            queue = (Queue) destination;
        }
        catch (ClassCastException cce)
        {
            // JMS queue proxy for JMS destination ''{0}'' has a destination type of ''{1}'' which is not Queue.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_QUEUE_DESTINATION, new Object[] { destinationJndiName, destination.getClass().getName() });
            throw me;
        }

        // Create connection
        try
        {
            QueueConnectionFactory queueFactory = (QueueConnectionFactory) connectionFactory;
            if (connectionCredentials != null)
                connection = queueFactory.createQueueConnection(connectionCredentials.getUsername(), connectionCredentials.getPassword());
            else
                connection = queueFactory.createQueueConnection();
        }
        catch (ClassCastException cce)
        {
            // JMS queue proxy for JMS destination ''{0}'' has a connection factory type of ''{1}'' which is not QueueConnectionFactory.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_QUEUE_FACTORY, new Object[] { destinationJndiName, connectionFactory.getClass().getName() });
            throw me;
        }

        // Create queue session on the connection
        QueueConnection queueConnection = (QueueConnection) connection;
        session = queueConnection.createQueueSession(false, getAcknowledgeMode());

        // Create sender on the queue session
        QueueSession queueSession = (QueueSession) session;
        sender = queueSession.createSender(queue);
        producer = sender;

        // Start the connection
        connection.start();
    }

    @Override
    void sendTextMessage(String text, Map properties) throws JMSException
    {
        if (text != null)
        {
            TextMessage message = session.createTextMessage();
            message.setText(text);
            copyHeadersToProperties(properties, message);
            long timeToLive = getTimeToLive(properties);
            sender.send(message, getDeliveryMode(), messagePriority, timeToLive);
        }
    }

    @Override
    void sendObjectMessage(Serializable obj, Map properties) throws JMSException
    {
        if (obj != null)
        {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(obj);
            copyHeadersToProperties(properties, message);
            long timeToLive = getTimeToLive(properties);
            sender.send(message, getDeliveryMode(), messagePriority, timeToLive);
        }
    }
}
