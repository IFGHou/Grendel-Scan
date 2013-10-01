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

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.NamingException;

import flex.messaging.MessageException;

/**
 * A <code>JMSProducer</code> subclass specifically for JMS Topic publishers.
 *
 * @exclude
 */
public class JMSTopicProducer extends JMSProducer
{
    /* JMS related variables */
    private TopicPublisher publisher;

    /**
     * Starts <code>JMSTopicProducer</code>.
     */
    @Override public void start() throws NamingException, JMSException
    {
        super.start();

        // Establish topic
        Topic topic = null;
        try
        {
            topic = (Topic)destination;
        }
        catch (ClassCastException cce)
        {
            // JMS topic proxy for JMS destination ''{0}'' has a destination type of ''{1}'' which is not Topic.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_TOPIC_DESTINATION, new Object[] {destinationJndiName, destination.getClass().getName()});
            throw me;
        }

        // Create connection
        try
        {
            TopicConnectionFactory topicFactory = (TopicConnectionFactory)connectionFactory;
            if (connectionCredentials != null)
                connection = topicFactory.createTopicConnection(connectionCredentials.getUsername(), connectionCredentials.getPassword());
            else
                connection = topicFactory.createTopicConnection();
        }
        catch (ClassCastException cce)
        {
            // JMS topic proxy for JMS destination ''{0}'' has a connection factory of type ''{1}'' which is not TopicConnectionFactory.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_TOPIC_FACTORY, new Object[] {destinationJndiName, connectionFactory.getClass().getName()});
            throw me;
        }

        // Create topic session on the connection
        TopicConnection topicConnection = (TopicConnection)connection;
        session = topicConnection.createTopicSession(false /* Always nontransacted */, getAcknowledgeMode());

        // Create publisher on the topic session
        TopicSession topicSession = (TopicSession)session;
        publisher = topicSession.createPublisher(topic);
        producer = publisher;

        // Start the connection
        connection.start();
    }

    @Override void sendObjectMessage(Serializable obj, Map properties) throws JMSException
    {
        if (obj != null)
        {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(obj);
            copyHeadersToProperties(properties, message);
            long timeToLive = getTimeToLive(properties);
            publisher.publish(message, getDeliveryMode(), messagePriority, timeToLive);
        }
    }

    @Override void sendTextMessage(String text, Map properties) throws JMSException
    {
        if (text != null)
        {
            TextMessage message = session.createTextMessage();
            message.setText(text);
            copyHeadersToProperties(properties, message);
            long timeToLive = getTimeToLive(properties);
            publisher.publish(message, getDeliveryMode(), messagePriority, timeToLive);
        }
    }
}
