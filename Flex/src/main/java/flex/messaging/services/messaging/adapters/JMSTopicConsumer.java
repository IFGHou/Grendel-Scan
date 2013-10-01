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
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.NamingException;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import flex.messaging.MessageException;
import flex.messaging.config.ConfigurationException;
import flex.messaging.log.Log;

/**
 * A <code>JMSConsumer</code> subclass specifically for JMS Topic subscribers.
 *
 * @exclude
 */
public class JMSTopicConsumer extends JMSConsumer
{
    protected boolean durableConsumers;
    protected String durableSubscriptionName;

    //--------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    //--------------------------------------------------------------------------

    /**
     * Initialize with settings from the JMS adapter.
     *
     * @param settings JMS settings to use for initialization.
     */
    @Override public void initialize(JMSSettings settings)
    {
        super.initialize(settings);
        durableConsumers = settings.useDurableConsumers();
    }

    /**
     * Verifies that the <code>JMSTopicConsumer</code> is in valid state before
     * it is started. For <code>JMSTopicConsumer</code> to be in valid state,
     * it needs durableSubscriptionName if durableConsumers is true.
     */
    @Override protected void validate()
    {
        super.validate();

        if (durableConsumers && durableSubscriptionName == null)
        {
            // JMS topic consumer for JMS destination ''{0}'' is configured to use durable subscriptions but it does not have a durable subscription name.
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(JMSConfigConstants.MISSING_DURABLE_SUBSCRIPTION_NAME, new Object[]{destinationJndiName});
            throw ce;
        }
    }

    /**
     * Starts the <code>JMSTopicConsumer</code>.
     *
     * @throws NamingException
     * @throws JMSException
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
        TopicConnectionFactory topicFactory;
        try
        {
            topicFactory = (TopicConnectionFactory)connectionFactory;
            if (connectionCredentials != null)
                connection = topicFactory.createTopicConnection(connectionCredentials.getUsername(), connectionCredentials.getPassword());
            else
                connection = topicFactory.createTopicConnection();
        }
        catch (ClassCastException cce)
        {
            // JMS topic proxy for JMS destination ''{0}'' has a connection factory type of ''{1}'' which is not TopicConnectionFactory.
            MessageException me = new MessageException();
            me.setMessage(JMSConfigConstants.NON_TOPIC_FACTORY, new Object[] {destinationJndiName, connectionFactory.getClass().getName()});
            throw me;
        }

        TopicConnection topicConnection = (TopicConnection)connection;

        if (durableConsumers)
        {
            try
            {
                if (Log.isDebug())
                    Log.getLogger(JMSAdapter.LOG_CATEGORY).debug("JMS consumer for JMS destination '"
                            + destinationJndiName + "' is setting its underlying connection's client id to "
                            + durableSubscriptionName + " for durable subscription.");

                topicConnection.setClientID(durableSubscriptionName);
            }
            catch (Exception e)
            {
                // Try to set the clientID in a seperate thread.
                ExecutorService clientIdSetter = Executors.newSingleThreadExecutor();
                ClientIdSetterCallable cisc = new ClientIdSetterCallable(topicFactory, durableSubscriptionName);
                Future future = clientIdSetter.submit(cisc);
                try
                {
                    topicConnection = (TopicConnection)future.get();
                }
                catch (InterruptedException ie)
                {
                    if (Log.isWarn())
                        Log.getLogger(JMSAdapter.LOG_CATEGORY).warn("The proxied durable JMS subscription with name, "
                                + durableSubscriptionName + " could not set its client id "
                                + "on the topic connection because it was interrupted: "
                                + ie.toString());
                }
                catch (ExecutionException ee)
                {
                    // JMS topic consumer for JMS destination ''{0}'' is configured to use durable subscriptions but the application server does not permit javax.jms.Connection.setClientID method needed to support durable subscribers. Set durable property to false.
                    MessageException me = new MessageException();
                    me.setMessage(JMSConfigConstants.DURABLE_SUBSCRIBER_NOT_SUPPORTED, new Object[]{destinationJndiName});
                    throw me;
                }
            }
        }

        // Create topic session on the connection
        session = topicConnection.createTopicSession(false, getAcknowledgeMode());
        TopicSession topicSession = (TopicSession) session;

        // Create subscriber on topic session, handling message selectors and durable subscribers
        if (selectorExpression != null)
        {
            if (durableConsumers && durableSubscriptionName != null)
                consumer = topicSession.createDurableSubscriber(topic, durableSubscriptionName, selectorExpression, false);
            else
                consumer = topicSession.createSubscriber(topic, selectorExpression, false);
        }
        else
        {
            if (durableConsumers && durableSubscriptionName != null)
                consumer = topicSession.createDurableSubscriber(topic, durableSubscriptionName);
            else
                consumer = topicSession.createSubscriber(topic);
        }

        startMessageReceiver();
    }

    /**
     * Stops the <code>JMSTopicConsumer</code> and unsubscribes a durable subscription
     * if one exists.
     *
     * @param unsubscribe Determines whether to unsubscribe a durable subscription
     * if one exists, or not.
     */
    @Override public void stop(boolean unsubscribe)
    {
        if (unsubscribe)
        {
            stopMessageReceiver();

            try
            {
                if (consumer != null)
                    consumer.close();
            }
            catch (Exception e)
            {
                if (Log.isWarn())
                    Log.getLogger(JMSAdapter.LOG_CATEGORY).warn("JMS consumer for JMS destination '"
                            + destinationJndiName + "' received an error while closing its underlying MessageConsumer: "
                            + e.getMessage());
            }

            if (durableConsumers)
            {
                try
                {
                    TopicSession topicSession = (TopicSession)session;
                    topicSession.unsubscribe(durableSubscriptionName);
                }
                catch (Exception e)
                {
                    if (Log.isWarn())
                        Log.getLogger(JMSAdapter.LOG_CATEGORY).warn("The proxied durable JMS subscription with name, "
                                + durableSubscriptionName + " failed to unsubscribe : "
                                + e.toString());
                }
            }
        }
        super.stop();
    }

    //--------------------------------------------------------------------------
    //
    // Public Getters and Setters for JMSConsumer properties
    //
    //--------------------------------------------------------------------------

    /**
     * Returns whether consumers are durable or not.
     *
     * @return <code>true</code> is consumers are durable, <code>false</code>
     * otherwise.
     */
    public boolean isDurableConsumers()
    {
        return durableConsumers;
    }

    /**
     * Sets whether consumers are durable or not. This property is optional
     * and defaults to false and it should not changed after startup.
     *
     * @param durableConsumers A boolean indicating whether consumers should be durable.
     */
    public void setDurableConsumers(boolean durableConsumers)
    {
        this.durableConsumers = durableConsumers;
    }

    /**
     * Returns the durable subscription name.
     *
     * @return The durable subscription name.
     */
    public String getDurableSubscriptionName()
    {
        return durableSubscriptionName;
    }

    /**
     * Sets the durable subscription name that is used as clientID on the
     * underlying connection when durable subscriptions are used. This property
     * should not changed after startup.
     *
     * @param durableSubscriptionName The durable subscription name.
     */
    public void setDurableSubscriptionName(String durableSubscriptionName)
    {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    //--------------------------------------------------------------------------
    //
    // Protected and Private APIs
    //
    //--------------------------------------------------------------------------

    /**
     * Helper thread class to circumvent a Web/EJB container from preventing us
     * from calling setClientId() on our topic connection. By spinning this out
     * into a short lived thread the container loses track of what we're doing.
     */
    class ClientIdSetterCallable implements Callable
    {
        private TopicConnectionFactory tcf;
        private String clientId;

        private TopicConnection topicConnection;

        public ClientIdSetterCallable(TopicConnectionFactory tcf, String clientId)
        {
            this.tcf = tcf;
            this.clientId = clientId;
        }

        @Override public Object call() throws JMSException
        {
            topicConnection = tcf.createTopicConnection();
            topicConnection.setClientID(clientId);
            return topicConnection;
        }
    }
}
