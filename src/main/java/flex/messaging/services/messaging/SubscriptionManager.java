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
package flex.messaging.services.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import flex.management.ManageableComponent;
import flex.messaging.FlexContext;
import flex.messaging.MessageClient;
import flex.messaging.MessageDestination;
import flex.messaging.MessageException;
import flex.messaging.client.FlexClient;
import flex.messaging.log.Log;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.Message;
import flex.messaging.security.MessagingSecurity;
import flex.messaging.services.MessageService;
import flex.messaging.services.ServiceAdapter;
import flex.messaging.services.ServiceException;
import flex.messaging.services.messaging.selector.JMSSelector;
import flex.messaging.services.messaging.selector.JMSSelectorException;
import flex.messaging.util.StringUtils;
import flex.messaging.util.TimeoutManager;

/**
 * The SubscriptionManager monitors subscribed clients for
 * MessageService and its subclasses, such as DataService.
 *
 * @author neville
 * @exclude
 */
public class SubscriptionManager extends ManageableComponent
{
    public static final String TYPE = "SubscriptionManager";
    private static final Object classMutex = new Object();
    private static int instanceCount = 0;

    protected final MessageDestination destination;
    private long subscriptionTimeoutMillis;

    /** clientId to MessageClient for any subscriber. */
    protected final Map allSubscriptions = new ConcurrentHashMap();
    /** Subscriptions with no subtopic. */
    private final TopicSubscription globalSubscribers = new TopicSubscription();
    /** Subscriptions with a simple subtopic. */
    private final Map subscribersPerSubtopic = new ConcurrentHashMap();
    /** Subscriptions with a wildcard subtopic. */
    private final Map subscribersPerSubtopicWildcard = new ConcurrentHashMap();

    private static final int SUBTOPICS_NOT_SUPPORTED = 10553;

    // We can either timeout subscriptions by session expiration (idleSubscriptionTimeout=0) or by an explicit
    // timeout.  If we time them out by timeout, this refers to the TimeoutManager
    // we use to monitor session timeouts.
    private TimeoutManager subscriberSessionManager;

    public SubscriptionManager(MessageDestination destination)
    {
        this(destination, false);
    }

    public SubscriptionManager(MessageDestination destination, boolean enableManagement)
    {
        super(enableManagement);
        synchronized (classMutex)
        {
            super.setId(TYPE + ++instanceCount);
        }
        this.destination = destination;

        subscriptionTimeoutMillis = 0;
    }

    // This component's id should never be changed as it's generated internally
    @Override public void setId(String id)
    {
        // No-op
    }

    /**
     * Stops the subscription manager.
     */
    @Override public void stop()
    {
        super.stop();

        // Remove management.
        if (isManaged() && getControl() != null)
        {
            getControl().unregister();
            setControl(null);
            setManaged(false);
        }

        // Destroy subscriptions
        synchronized (this)
        {
            if (!allSubscriptions.isEmpty())
            {
                Iterator iter = allSubscriptions.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry subscription = (Map.Entry)iter.next();
                    removeSubscriber((MessageClient)subscription.getValue());
                }
            }
        }
    }

    public void setSubscriptionTimeoutMillis(long value)
    {
        subscriptionTimeoutMillis = value;
        if (subscriptionTimeoutMillis > 0)
        {
            subscriberSessionManager = new TimeoutManager();
        }
    }

    public long getSubscriptionTimeoutMillis()
    {
        return subscriptionTimeoutMillis;
    }

    /**
     * Implement a serializer instance which wraps the subscription
     * manager in a transient variable.  It will need to block out
     * all sub/unsub messages before they are broadcast to the
     * remote server, iterate through the maps of subscriptions and
     * for each "unique" subscription it writes the selector and
     * subtopic.
     *
     * synchronization note: this assumes no add/remove subscriptions
     * are occuring while this method is called.
     */
    public Object getSubscriptionState()
    {
        ArrayList subState = new ArrayList();

        if (globalSubscribers.defaultSubscriptions != null &&
            !globalSubscribers.defaultSubscriptions.isEmpty())
        {
            subState.add(null); // selector string
            subState.add(null); // subtopic string
        }
        if (globalSubscribers.selectorSubscriptions != null)
        {
            for (Iterator it = globalSubscribers.selectorSubscriptions.keySet().iterator();
                        it.hasNext(); )
            {
                subState.add(it.next());
                subState.add(null); // subtopic
            }
        }
        addSubscriptionState(subState, subscribersPerSubtopic);
        addSubscriptionState(subState, subscribersPerSubtopicWildcard);

        if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Retrieved subscription state to send to new cluster member for destination: " + destination.getId() + ": " + StringUtils.NEWLINE + subState);

        return subState;
    }

    private void addSubscriptionState(List subState, Map subsPerSubtopic)
    {
        for (Iterator it = subsPerSubtopic.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            Subtopic subtopic = (Subtopic) entry.getKey();
            TopicSubscription tc = (TopicSubscription) entry.getValue();

            if (tc.defaultSubscriptions != null && !tc.defaultSubscriptions.isEmpty())
            {
                subState.add(null);
                subState.add(subtopic.toString());
            }
            if (tc.selectorSubscriptions != null)
            {
                for (Iterator sit = tc.selectorSubscriptions.keySet().iterator(); sit.hasNext(); )
                {
                    subState.add(sit.next());
                    subState.add(subtopic.toString()); // subtopic
                }
            }
        }

    }

    protected String getDebugSubscriptionState()
    {
        StringBuffer sb = new StringBuffer();

        sb.append(" global subscriptions: " + globalSubscribers + StringUtils.NEWLINE);
        sb.append(" regular subtopic subscriptions: " + subscribersPerSubtopic + StringUtils.NEWLINE);
        sb.append(" wildcard subtopic subscriptions: " + subscribersPerSubtopicWildcard + StringUtils.NEWLINE);
        return sb.toString();
    }

    public Set getSubscriberIds()
    {
        return allSubscriptions.keySet();
    }

    public Set getSubscriberIds(Message message, boolean evalSelector)
    {
        Set ids = new LinkedHashSet();

        Object subtopicObj = message.getHeader(AsyncMessage.SUBTOPIC_HEADER_NAME);

        if (subtopicObj instanceof Object[])
            subtopicObj = Arrays.asList((Object[])subtopicObj);

        if (subtopicObj instanceof String)
        {
            String subtopicString = (String) subtopicObj;

            if (subtopicString.length() > 0)
                addSubtopicSubscribers(subtopicString, message, ids, evalSelector);
            else
                addTopicSubscribers(globalSubscribers, message, ids, evalSelector);
        }
        else if (subtopicObj instanceof List)
        {
            List subtopicList = (List) subtopicObj;
            for (int i = 0; i < subtopicList.size(); i++)
                addSubtopicSubscribers((String) subtopicList.get(i), message, ids, evalSelector);
        }
        else
            addTopicSubscribers(globalSubscribers, message, ids, evalSelector);

        return ids;
    }

    public Set getSubscriberIds(String subtopicPattern, Map messageHeaders)
    {
        // This could be more efficient but we'd have to change the SQLParser
        // to accept a map.
        Message msg = new AsyncMessage();
        msg.setHeader(AsyncMessage.SUBTOPIC_HEADER_NAME, subtopicPattern);
        if (messageHeaders != null)
            msg.setHeaders(messageHeaders);
        return getSubscriberIds(msg, true);
    }

    void addSubtopicSubscribers(String subtopicString, Message message, Set ids, boolean evalSelector)
    {
        // If we have a subtopic, we need to route the message only to that
        // subset of subscribers.
        if (!destination.getServerSettings().getAllowSubtopics())
        {
            // Throw an error - the destination doesn't allow subtopics.
            ServiceException se = new ServiceException();
            se.setMessage(SUBTOPICS_NOT_SUPPORTED, new Object[] {subtopicString, destination.getId()});
            throw se;
        }
        Subtopic subtopic = getSubtopic(subtopicString);
        // Give a MessagingAdapter a chance to block the send to this subtopic.
        ServiceAdapter adapter = destination.getAdapter();
        if (adapter instanceof MessagingSecurity)
        {
            if (!((MessagingSecurity)adapter).allowSend(subtopic))
            {
                ServiceException se = new ServiceException();
                se.setMessage(10558, new Object[] {subtopicString});
                throw se;
            }
        }

        TopicSubscription ts = (TopicSubscription) subscribersPerSubtopic.get(subtopic);
        addTopicSubscribers(ts, message, ids, evalSelector);

        /*
         * TODO: performance - organize these into a tree so we can find consumers via
         * a hashtable lookup rather than a linear search
         */
        Set subtopics = subscribersPerSubtopicWildcard.keySet();
        for (Iterator iter = subtopics.iterator(); iter.hasNext(); )
        {
            Subtopic st = (Subtopic) iter.next();
            if (st.matches(subtopic))
            {
                ts = (TopicSubscription) subscribersPerSubtopicWildcard.get(st);
                addTopicSubscribers(ts, message, ids, evalSelector);
            }
        }
    }

    void addTopicSubscribers(TopicSubscription ts, Message message, Set ids, boolean evalSelector)
    {
        if (ts == null)
            return;

        Map subs = ts.defaultSubscriptions;
        if (subs != null)
            ids.addAll(subs.keySet());
        if (ts.selectorSubscriptions != null)
        {
            for (Iterator sit = ts.selectorSubscriptions.entrySet().iterator(); sit.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) sit.next();
                String selector = (String) entry.getKey();
                subs = (Map) entry.getValue();

                if (!evalSelector)
                    ids.addAll(subs.keySet());
                else
                {
                    JMSSelector jmsSel = new JMSSelector(selector);

                    try
                    {
                        if (jmsSel.match(message))
                            ids.addAll(subs.keySet());
                    }
                    catch (JMSSelectorException jmse)
                    {
                        if (Log.isWarn())
                            Log.getLogger(JMSSelector.LOG_CATEGORY).warn("Error processing message selector: " +
                                 jmsSel + StringUtils.NEWLINE +
                                 "  incomingMessage: " + message + StringUtils.NEWLINE +
                                 "  selector: " + selector);
                    }
                }
            }
        }
    }

    /**
     * Returns the requested subscriber.
     * If the subscriber exists it is also registered for subscription timeout if necessary.
     * If the subscriber is not found this method returns null.
     *
     * @param clientId The clientId of the target subscriber.
     * @return The subscriber, or null if the subscriber is not found.
     */
    public MessageClient getSubscriber(Object clientId)
    {
        MessageClient client = (MessageClient) allSubscriptions.get(clientId);
        if (client != null && !client.isTimingOut())
            monitorTimeout(client);
        return client;
    }

    /**
     * Removes the subscriber, unsubscribing it from all current subscriptions.
     * This is used by the admin UI.
     */
    public void removeSubscriber(MessageClient client)
    {
        // Sends unsub messages for each subscription for this MessageClient which
        // should mean we remove the client at the end.
        client.invalidate();

        if (getSubscriber(client.getClientId()) != null)
            Log.getLogger(MessageService.LOG_CATEGORY).error("Failed to remove client: " + client.getClientId());
    }

    public void addSubscriber(Object clientId, String selector, String subtopicString, String endpointId)
    {
        Subtopic subtopic = getSubtopic(subtopicString);
        MessageClient client = null;
        TopicSubscription topicSub;
        Map subs;
        Map map;

        try
        {
            // Handle resubscribes from the same client and duplicate subscribes from different clients
            boolean subscriptionAlreadyExists = (getSubscriber(clientId) != null);
            client = getMessageClient(clientId, endpointId);

            FlexClient flexClient = FlexContext.getFlexClient();
            if (subscriptionAlreadyExists)
            {
                // Block duplicate subscriptions from multiple FlexClients if they
                // attempt to use the same clientId.  (when this is called from a remote
                // subscription, there won't be a flex client so skip this test).
                if (flexClient != null && !flexClient.getId().equals(client.getFlexClient().getId()))
                {
                    ServiceException se = new ServiceException();
                    se.setMessage(10559, new Object[] {clientId});
                    throw se;
                }

                // It's a resubscribe. Reset the endpoint push state for the subscription to make sure its current
                // because a resubscribe could be arriving over a new endpoint or a new session.
                client.resetEndpoint(endpointId);
            }

            ServiceAdapter adapter = destination.getAdapter();
            client.updateLastUse();

            if (subtopic == null)
            {
                topicSub = globalSubscribers;
            }
            else
            {
                if (!destination.getServerSettings().getAllowSubtopics())
                {
                    // Throw an error - the destination doesn't allow subtopics.
                    ServiceException se = new ServiceException();
                    se.setMessage(SUBTOPICS_NOT_SUPPORTED, new Object[] {subtopicString, destination.getId()});
                    throw se;
                }
                // Give a MessagingAdapter a chance to block the subscribe.
                if ((adapter instanceof MessagingSecurity) && (subtopic != null))
                {
                    if (!((MessagingSecurity)adapter).allowSubscribe(subtopic))
                    {
                        ServiceException se = new ServiceException();
                        se.setMessage(10557, new Object[] {subtopicString});
                        throw se;
                    }
                }

                /*
                 * If there is a wildcard, we always need to match that subscription
                 * against the producer.  If it has no wildcard, we can do a quick
                 * lookup to find the subscribers.
                 */
                if (subtopic.containsSubtopicWildcard())
                    map = subscribersPerSubtopicWildcard;
                else
                    map = subscribersPerSubtopic;

                topicSub = (TopicSubscription) map.get(subtopic);

                if (topicSub == null)
                {
                    synchronized (this)
                    {
                        topicSub = (TopicSubscription) map.get(subtopic);
                        if (topicSub == null)
                        {
                            topicSub = new TopicSubscription();
                            map.put(subtopic, topicSub);
                        }
                    }
                }
            }

            /* Subscribing with no selector */
            if (selector == null)
            {
                subs = topicSub.defaultSubscriptions;
                if (subs == null)
                {
                    synchronized (this)
                    {
                        if ((subs = topicSub.defaultSubscriptions) == null)
                            topicSub.defaultSubscriptions = subs = new ConcurrentHashMap();
                    }
                }
            }
            /* Subscribing with a selector - store all subscriptions under the selector key */
            else
            {
                if (topicSub.selectorSubscriptions == null)
                {
                    synchronized (this)
                    {
                        if (topicSub.selectorSubscriptions == null)
                            topicSub.selectorSubscriptions = new ConcurrentHashMap();
                    }
                }
                subs = (Map) topicSub.selectorSubscriptions.get(selector);
                if (subs == null)
                {
                    synchronized (this)
                    {
                        if ((subs = (Map) topicSub.selectorSubscriptions.get(selector)) == null)
                            topicSub.selectorSubscriptions.put(selector, subs = new ConcurrentHashMap());
                    }
                }
            }

            if (subs.containsKey(clientId))
            {
                /* I'd rather this be an error but in 2.0 we allowed this without error */
                if (Log.isWarn())
                    Log.getLogger(JMSSelector.LOG_CATEGORY).warn("Client: " + clientId + " already subscribed to: " + destination.getId() + " selector: " + selector + " subtopic: " + subtopicString);
            }
            else
            {
                client.addSubscription(selector, subtopicString);
                synchronized (this)
                {
                    /*
                     * Make sure other members of the cluster know that we are subscribed to
                     * this info if we are in server-to-server mode
                     *
                     * This has to be done in the synchronized section so that we properly
                     * order subscribe and unsubscribe messages for our peers so their
                     * subscription state matches the one in the local server.
                     */
                    if (subs.isEmpty() && destination.isClustered() &&
                        !destination.getServerSettings().isBroadcastRoutingMode())
                        sendSubscriptionToPeer(true, selector, subtopicString);
                    subs.put(clientId, client);
                }
                monitorTimeout(client); // local operation, timeouts on remote host are not started until failover

                // Finally, if a new MessageClient was created, notify its created
                // listeners now that MessageClient's subscription state is setup.
                if (!subscriptionAlreadyExists)
                    client.notifyCreatedListeners();
            }
        }
        finally {
            releaseMessageClient(client);
        }

    }

    public void removeSubscriber(Object clientId, String selector, String subtopicString, String endpointId)
    {
        MessageClient client = null;
        try
        {            
            synchronized (allSubscriptions)
            {
                // Do a simple lookup first to avoid the creation of a new MessageClient instance
                // in the following call to getMessageClient() if the subscription is already removed.
                client = (MessageClient)allSubscriptions.get(clientId);
                if (client == null) // Subscription was already removed.
                {    
                    return;
                }
                else // Re-get in order to track refs correctly.
                {
                    client = getMessageClient(clientId, endpointId);
                }
            }

            Subtopic subtopic = getSubtopic(subtopicString);
            TopicSubscription topicSub;
            Map subs;
            Map map = null;

            if (subtopic == null)
            {
                topicSub = globalSubscribers;
            }
            else
            {
                if (subtopic.containsSubtopicWildcard())
                    map = subscribersPerSubtopicWildcard;
                else
                    map = subscribersPerSubtopic;

                topicSub = (TopicSubscription) map.get(subtopic);

                if (topicSub == null)
                    throw new MessageException("Client: " + clientId + " not subscribed to subtopic: " + subtopic);
            }

            if (selector == null)
                subs = topicSub.defaultSubscriptions;
            else
                subs = (Map) topicSub.selectorSubscriptions.get(selector);

            if (subs == null || subs.get(clientId) == null)
                throw new MessageException("Client: " + clientId + " not subscribed to destination with selector: " + selector);

            synchronized (this)
            {
                subs.remove(clientId);
                if (subs.isEmpty() &&
                    destination.isClustered() && !destination.getServerSettings().isBroadcastRoutingMode())
                    sendSubscriptionToPeer(false, selector, subtopicString);

                if (subs.isEmpty())
                {
                    if (selector != null)
                    {
                        if (topicSub.selectorSubscriptions != null && topicSub.selectorSubscriptions.isEmpty())
                            topicSub.selectorSubscriptions.remove(selector);
                    }

                    if (subtopic != null &&
                        (topicSub.selectorSubscriptions == null || topicSub.selectorSubscriptions.isEmpty()) &&
                        (topicSub.defaultSubscriptions == null || topicSub.defaultSubscriptions.isEmpty()))
                    {
                           if ((topicSub.selectorSubscriptions == null || topicSub.selectorSubscriptions.isEmpty()) &&
                               (topicSub.defaultSubscriptions == null || topicSub.defaultSubscriptions.isEmpty()))
                               map.remove(subtopic);
                    }
                }
            }

            if (client.removeSubscription(selector, subtopicString))
            {
                allSubscriptions.remove(clientId);
                client.invalidate(); // Destroy the MessageClient.
            }
        }
        finally
        {
            if (client != null)
                releaseMessageClient(client);
        }
    }

    protected MessageClient newMessageClient(Object clientId, String endpointId)
    {
        return new MessageClient(clientId, destination, endpointId);
    }

    /**
     * This method is used for subscribers who maintain client ids in their
     * own subscription tables.  It ensures we have the MessageClient for
     * a given clientId for as long as this session is valid (or the
     * subscription times out).
     */
    public MessageClient registerMessageClient(Object clientId, String endpointId)
    {
        MessageClient client = getMessageClient(clientId, endpointId);

        monitorTimeout(client);

        /*
         * There is only one reference to the MessageClient for the
         * registered flag.  If someone happens to register the
         * same client more than once, just allow that to add one reference.
         */
        if (client.isRegistered())
            releaseMessageClient(client);
        else
            client.setRegistered(true);

        return client;
    }

    public MessageClient getMessageClient(Object clientId, String endpointId)
    {
        synchronized (allSubscriptions)
        {
            MessageClient client = (MessageClient) allSubscriptions.get(clientId);
            if (client == null)
            {
                client = newMessageClient(clientId, endpointId);
                allSubscriptions.put(clientId, client);
            }

            client.incrementReferences();
            return client;
        }
    }

    public void releaseMessageClient(MessageClient client)
    {
        if (client == null)
            return;

        synchronized (allSubscriptions)
        {
            if (client.decrementReferences())
            {
                allSubscriptions.remove(client.getClientId());
                client.invalidate(); // Destroy the MessageClient.
            }
        }
    }

    protected void monitorTimeout(MessageClient client)
    {
        if (subscriberSessionManager != null)
        {
            synchronized (client)
            {
                if (!client.isTimingOut())
                {
                    subscriberSessionManager.scheduleTimeout(client);
                    client.setTimingOut(true);
                }
            }
        }
    }

    private Subtopic getSubtopic(String subtopic)
    {
        if (subtopic == null)
            return null;

        return new Subtopic(subtopic, destination.getServerSettings().getSubtopicSeparator());
    }

    /**
     * Broadcast this subscribe/unsubscribe message to the cluster so everyone is aware
     * of this server's interest in messages matching this selector and subtopic.
     */
    protected void sendSubscriptionToPeer(boolean subscribe, String selector, String subtopic)
    {
        if (Log.isDebug())
            Log.getLogger(MessageService.LOG_CATEGORY).debug("Sending subscription to peers for subscribe? " + subscribe + " selector: " + selector + " subtopic: " + subtopic);

        ((MessageService)destination.getService()).sendSubscribeFromPeer(destination.getId(),
                                    subscribe ? Boolean.TRUE : Boolean.FALSE, selector, subtopic);
    }

    static class TopicSubscription {
        /** This is the Map of clientId to MessageClient for each client subscribed to this topic with no selector. */
        Map defaultSubscriptions;

        /** A map of selector string to Map of clientId to MessageClient. */
        Map selectorSubscriptions;

        @Override public String toString()
        {
            StringBuffer sb = new StringBuffer();

            sb.append("default subscriptions: " + defaultSubscriptions + StringUtils.NEWLINE);
            sb.append("selector subscriptions: " + selectorSubscriptions + StringUtils.NEWLINE);
            return sb.toString();
        }
    }

    @Override protected String getLogCategory()
    {
        return MessageService.LOG_CATEGORY;
    }
}
