/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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
package flex.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;
import flex.messaging.client.FlexClient;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.services.MessageService;
import flex.messaging.services.messaging.Subtopic;
import flex.messaging.services.messaging.selector.JMSSelector;
import flex.messaging.services.messaging.selector.JMSSelectorException;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.StringUtils;
import flex.messaging.util.TimeoutAbstractObject;

/**
 * Represents a client-side MessageAgent instance.
 * Currently a server-side MessageClient is only created if its client-side counterpart has subscribed
 * to a destination for pushed data (e.g. Consumer). Client-side Producers do not result in the creation of
 * corresponding server-side MessageClient instances.
 * 
 * Client-side MessageAgents communicate with the server over a Channel that corresponds to a FlexSession.
 * Server-side MessageClient instances are always created in the context of a FlexSession and when the FlexSession 
 * is invalidated any associated MessageClients are invalidated as well.
 * 
 * MessageClients may also be timed out on a per-destination basis and this is based on subscription inactivity.
 * If no messages are pushed to the MessageClient within the destination's subscription timeout period the
 * MessageClient will be shutdown even if the associated FlexSession is still active and connected.
 * Per-destination subscription timeout is an optional configuration setting, and should only be used when inactive
 * subscriptions should be shut down opportunistically to preserve server resources.
 */
public class MessageClient extends TimeoutAbstractObject implements Serializable
{
    //--------------------------------------------------------------------------
    //
    // Public Static Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Log category for MessageClient related messages.
     */
    public static final String MESSAGE_CLIENT_LOG_CATEGORY = LogCategories.CLIENT_MESSAGECLIENT;     
    
    //--------------------------------------------------------------------------
    //
    // Static Constants
    //
    //--------------------------------------------------------------------------
    
    /**
     * Serializable to support broadcasting subscription state across the cluster for
     * optimized message routing.
     */
    static final long serialVersionUID = 3730240451524954453L;

    //--------------------------------------------------------------------------
    //
    // Static Variables
    //
    //--------------------------------------------------------------------------    
    
    /**
     * The list of MessageClient created listeners.
     */
    private static final CopyOnWriteArrayList createdListeners = new CopyOnWriteArrayList();
    
    //--------------------------------------------------------------------------
    //
    // Static Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Adds a MessageClient created listener.
     *
     * @see flex.messaging.MessageClientListener
     *
     * @param listener The listener to add.
     */
    public static void addMessageClientCreatedListener(MessageClientListener listener)
    {
        if (listener != null)
            createdListeners.addIfAbsent(listener);
    }    
    

// TODO UCdetector: Remove unused code: 
//     /**
//      * Removes a MessageClient created listener.
//      *
//      * @see flex.messaging.MessageClientListener
//      *
//      * @param listener The listener to remove.
//      */
//     public static void removeMessageClientCreatedListener(MessageClientListener listener)
//     {
//         if (listener != null)
//             createdListeners.remove(listener);
//     }

    //--------------------------------------------------------------------------
    //
    // Private Static Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Utility method.
     */
    private static boolean equalStrings(String a, String b) 
    {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Utility method.
     */
    private static int compareStrings(String a, String b)
    {
        if (a == b) 
            return 0;

        if (a != null && b != null)
            return a.compareTo(b);

        if (a == null)
            return -1;

        return 1;
    }    
    
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * @exclude
     * Constructs a new MessageClient for local use.
     * 
     * @param clientId The clientId for the MessageClient.
     * @param destination The destination the MessageClient is subscribed to.
     * @param endpointId The Id of the endpoint this MessageClient subscription was created over.
     */
    public MessageClient(Object clientId, MessageDestination destination, String endpointId)
    {
        this(clientId, destination, endpointId, true);
    }    
    
    /**
     * @exclude
     * Constructs a new MessageClient.
     * 
     * @param clientId The clientId for the MessageClient.
     * @param destination The destination the MessageClient is subscribed to.
     * @param endpointId The Id of the endpoint this MessageClient subscription was created over.
     * @param useSession RemoteMessageClient instances should not be associated with a FlexSession (pass false).
     */
    public MessageClient(Object clientId, MessageDestination destination, String endpointId, boolean useSession)
    {
        valid = true;        
        this.clientId = clientId;
        this.destination = destination;
        this.endpointId = endpointId;
        destinationId = destination.getId();
        updateLastUse(); // Initialize last use timestamp to construct time.

        /* If this is for a remote server, we do not associate with the session. */
        if (useSession)
        {
            flexSession = FlexContext.getFlexSession();
            flexSession.registerMessageClient(this);

            flexClient = FlexContext.getFlexClient();
            flexClient.registerMessageClient(this);
            
            // SubscriptionManager will notify the created listeners, once 
            // subscription state is setup completely.
            // notifyCreatedListeners();
        }
        else
        {
            flexClient = null;
            flexSession = null;
            // Use an instance level lock.
            lock = new Object();           
            // On a remote server we don't notify created listeners.
        }

        if (Log.isDebug())
            Log.getLogger(MESSAGE_CLIENT_LOG_CATEGORY).debug("MessageClient created with clientId '" + this.clientId + "' for destination '" + destinationId + "'.");        
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------    
    
    /**
     *  This flag is set to true when the client channel that this subscription was
     *  established over is disconnected.
     *  It supports cleaning up per-endpoint outbound queues maintained by the FlexClient.
     *  If the client notifies the server that its channel is disconnecting, the FlexClient
     *  does not need to maintain an outbound queue containing a subscription invalidation
     *  message for this MessageClient to send to the client.
     */
    private volatile boolean clientChannelDisconnected;
    
    /**
     *  The clientId for the MessageClient.
     *  This value is specified by the client directly or is autogenerated on the client.
     */
    protected final Object clientId;

    /**
     * Internal reference to the associated MessageDestination; don't expose this in the public API.
     */
    protected final MessageDestination destination;
    
    /**
     *  The destination the MessageClient is subscribed to. 
     */
    protected final String destinationId;

    /**
     * The set of session destroy listeners to notify when the session is destroyed.
     */
     private transient volatile CopyOnWriteArrayList destroyedListeners;
    
    /**
     * The Id for the endpoint this MessageClient subscription was created over.
     */
    private String endpointId;
    
    /**
     * The FlexClient associated with the MessageClient.
     */
    private final transient FlexClient flexClient;
    
    /**
     * The FlexSession associated with the MessageClient.
     * Not final because this needs to be reset if the subscription fails over to a new endpoint.
     */
    private transient FlexSession flexSession;
    
    /**
     * Flag used to break cycles during invalidation.
     */
    private boolean invalidating;
    
    /**
     * The lock to use to guard all state changes for the MessageClient.
     */
    protected Object lock = new Object();
    
    /**
     * Flag indicating whether the MessageClient is attempting to notify the remote client of
     * its invalidation.
     */
    private volatile boolean attemptingInvalidationClientNotification;
    
    /**
     * A counter used to control invalidation for a MessageClient that has multiple
     * subscriptions to its destination.
     * Unsubscribing from one will not invalidate the MessageClient as long as other
     * subscriptions remain active.
     */
    private transient int numReferences;
    
    /**
     * A set of all of the subscriptions managed by this message client.
     */
    protected final Set subscriptions = new CopyOnWriteArraySet();
    
    /** 
     * Flag indicating whether this client is valid or not.
     */
    protected boolean valid;    
    
    /**
     * Flag that indicates whether the MessageClient has a per-destination subscription timeout.
     * If false, the MessageClient will remain valid until its associated FlexSession is invalidated.
     */
    private volatile boolean willTimeout;

    /**
     * Has anyone explicitly registered this message client.  This indicates that
     * there is a reference to this MessageClient which is not an explicit subscription.
     * This is a hook for FDMS and other adapters which want to use pushMessageToClients
     * with clientIds but that do not want the subscription manager to manage subscriptions
     * for them.
     */
    private volatile boolean registered = false;

    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------    
    
    /**
     * Returns the clientId for the MessageClient.
     * 
     * @return The clientId for the MessageClient.
     */
    public Object getClientId()
    {
        return clientId; // Field is final; no need to sync.
    }
    
    /**
     * Returns the destination the MessageClient is subscribed to.
     * 
     * @return The destination the MessageClient is subscribed to.
     */
    public String getDestinationId()
    {
        return destinationId; // Field is final; no need to sync.
    }
    
    /**
     * Returns the Id for the endpoint the MessageClient subscription was created over.
     * 
     * @return The Id for the endpoint the MessageClient subscription was created over.
     */
    public String getEndpointId()
    {
        return endpointId; // Field is final; no need to sync.
    }
    
    /**
     * Returns the FlexClient associated with this MessageClient.
     * 
     * @return The FlexClient assocaited with this MessageClient.
     */
    public FlexClient getFlexClient()
    {
        return flexClient; // Field is final; no need to sync.
    }
    
    /**
     * Returns the FlexSession associated with this MessageClient.
     * 
     * @return The FlexSession associated with this MessageClient.
     */
    public FlexSession getFlexSession()
    {
        synchronized (lock)
        {
            return flexSession;
        }
    }
    
    /**
     * Returns the number of subscriptions associated with this MessageClient.
     *
     * @return The number of subscriptions associated with this MessageClient.
     */
    public int getSubscriptionCount()
    {
        int count;
        
        synchronized (lock)
        {
            count = (subscriptions != null) ? subscriptions.size() : 0;
        }
        
        return count;
    }    
    
    /**
     * @exclude
     * This is used for FlexClient outbound queue management. When a MessageClient is invalidated
     * if it is attempting to notify the client, then we must leave the outbound queue containing
     * the notification in place. Otherwise, any messages queued for the subscription may be
     * removed from the queue and possibly shut down immediately.
     */
    public boolean isAttemptingInvalidationClientNotification()
    {
        return attemptingInvalidationClientNotification;
    }
    
    /**
     * @exclude
     * This is set to true when the MessageClient is invalidated due to the client
     * channel the subscription was established over disconnecting.
     * It allows the FlexClient class to cleanup the outbound queue for the channel's
     * corresponding server endpoint for the remote client, because we know that no
     * currently queued messages need to be retained for delivery.
     */
    public void setClientChannelDisconnected(boolean value)
    {
        clientChannelDisconnected = value;
    }
    
    /**
     * @exclude
     */
    public boolean isClientChannelDisconnected()
    {
        return clientChannelDisconnected;
    }

    /**
     * @exclude
     * This is true when some code other than the SubscriptionManager 
     * is maintaining subscriptions for this message client.  It ensures
     * that we have this MessageClient kept around until the session 
     * expires.
     */
    public void setRegistered(boolean reg)
    {
        registered = reg;
    }
    
    /**
     * @exclude
     */
    public boolean isRegistered()
    {
        return registered;
    }
    
    /**
     * Adds a MessageClient destroy listener.
     *
     * @see flex.messaging.MessageClientListener
     *
     * @param listener The listener to add.
     */
    public void addMessageClientDestroyedListener(MessageClientListener listener)
    {        
        if (listener != null)
        {
            checkValid();
            
            if (destroyedListeners == null)
            {
                synchronized (lock)
                {
                    if (destroyedListeners == null)
                        destroyedListeners = new CopyOnWriteArrayList();
                }
            }
                        
            destroyedListeners.addIfAbsent(listener);
        }
    }
    
    /**
     * Removes a MessageClient destroyed listener.
     *
     * @see flex.messaging.MessageClientListener
     *
     * @param listener The listener to remove.
     */
    public void removeMessageClientDestroyedListener(MessageClientListener listener)
    {       
        // No need to check validity; removing a listener is always ok.
        if (listener != null && destroyedListeners != null)
            destroyedListeners.remove(listener);
    }    
    
    /**
     * @exclude
     * Adds a subscription to the subscription set for this MessageClient.
     * 
     * @param selector The selector expression used for the subscription.
     * @param subtopic The subtopic used for the subscription.
     */
    public void addSubscription(String selector, String subtopic)
    {
        synchronized (lock)
        {
            checkValid();
            
            incrementReferences();
            subscriptions.add(new SubscriptionInfo(selector, subtopic));
        }
    }
    
    /**
     * @exclude
     * Removes a subscription from the subscription set for this MessageClient.
     * 
     * @param selector The selector expression for the subscription.
     * @param subtopic The subtopic for the subscription.
     * @return true if no subscriptions remain for this MessageClient; otherwise false.
     */
    public boolean removeSubscription(String selector, String subtopic)
    {
        synchronized (lock)
        {
            if (subscriptions.remove(new SubscriptionInfo(selector, subtopic)))
                return decrementReferences();
            else if (Log.isError())
                Log.getLogger(MessageService.LOG_CATEGORY).error("Error - unable to find subscription to remove for MessageClient: " + clientId + " selector: " + selector + " subtopic: " + subtopic);
            return numReferences == 0;            
        }
    }
    
    /** 
     * @exclude
     * We use the same MessageClient for more than one subscription with different
     * selection criteria.  This tracks the number of subscriptions that are active
     * so that we know when we are finished.  
     */
    public void incrementReferences()
    {
        synchronized (lock)
        {
            numReferences++;
        }
    }    

    /** 
     * @exclude
     * Decrements the numReferences variable and returns true if this was the last reference. 
     */
    public boolean decrementReferences() 
    {
        synchronized (lock)
        {
            if (--numReferences == 0)
            {
                cancelTimeout();
                if (destination.getThrottleManager() != null)
                    destination.getThrottleManager().removeClientThrottleMark(clientId);
                return true;
            }
            return false;            
        }
    }

    /**
     * @exclude
     * Invoked by SubscriptionManager once the subscription state is setup completely
     * for the MessageClient.. 
     */
    public void notifyCreatedListeners()
    {
        // Notify MessageClient created listeners.
        if (!createdListeners.isEmpty())
        {
            // CopyOnWriteArrayList is iteration-safe from ConcurrentModificationExceptions.
            for (Iterator iter = createdListeners.iterator(); iter.hasNext();)
                ((MessageClientListener)iter.next()).messageClientCreated(this);
        }
    }    

    /**
     * @exclude
     * Invoked by SubscriptionManager while handling a subscribe request.
     * If the request is updating an existing subscription the 'push' state in the associated FlexClient
     * may need to be updated to ensure that the correct endpoint is used for this subscription.
     * 
     * @param newEndpointId The id for the new endpoint that the subscription may have failed over to.
     */
    public void resetEndpoint(String newEndpointId)
    {
        String oldEndpointId = null;
        FlexSession oldSession = null;
        FlexSession newSession = FlexContext.getFlexSession();
        synchronized (lock)
        {            
            // If anything is null, or nothing has changed, no need for a reset.
            if (endpointId == null || newEndpointId == null || flexSession == null || newSession == null || (endpointId.equals(newEndpointId) && flexSession.equals(newSession)))
                return;
            
            oldEndpointId = endpointId;
            endpointId = newEndpointId;

            oldSession = flexSession;
            flexSession = newSession;
        }        
        
        // Unregister in order to reset the proper push settings in the re-registration below once the session association has been patched.
        if (flexClient != null)
            flexClient.unregisterMessageClient(this);
        
        // Clear out any reference to this subscription that the previously associated session has.
        if (oldSession != null)
            oldSession.unregisterMessageClient(this);        
        
        // Associate the current session with this subscription.
        if (flexSession != null)
            flexSession.registerMessageClient(this);
    
        // Reset proper push settings.
        if (flexClient != null)
            flexClient.registerMessageClient(this);
        
        if (Log.isDebug())
        {
            String msg = "MessageClient with clientId '" + clientId + "' for destination '" + destinationId + "' has been reset as a result of a resubscribe.";
            if (oldEndpointId != null && !oldEndpointId.equals(newEndpointId))
                msg += " Endpoint change [" + oldEndpointId + " -> " + newEndpointId + "]";
            if ((oldSession != null) && (newSession != null) && (oldSession != newSession)) // Test identity.
                msg += " FlexSession change [" + oldSession.getClass().getName() + ":" + oldSession.getId() + " -> " + newSession.getClass().getName() + ":" + newSession.getId() + "]";
            
            Log.getLogger(MESSAGE_CLIENT_LOG_CATEGORY).debug(msg);    
        }            
    }
    
    /**
     * @exclude
     * Used to test whether this client should receive this message
     * based on the list of subscriptions we have recorded for it.
     * It must match both the subtopic and the selector expression.
     * Usually this is done by the subscription manager - this logic is
     * only here to maintain api compatibility with one of the variants
     * of the pushMessageToClients which has subscriberIds and an evalSelector
     * property.
     * 
     * @param message The message to test.
     */
    public boolean testMessage(Message message, MessageDestination destination) 
    {
        String subtopic = (String) message.getHeader(AsyncMessage.SUBTOPIC_HEADER_NAME);
        synchronized (lock)
        {
            for (Iterator it = subscriptions.iterator(); it.hasNext(); )
            {
                SubscriptionInfo si = (SubscriptionInfo) it.next();
                String csel = si.selector;
                String csub = si.subtopic;

                if ((subtopic == null && csub != null) || (subtopic != null && csub == null))
                    continue; // If either defines a subtopic, they both must define one.
                
                // If both define a subtopic, they must match.
                if (subtopic != null && csub != null)
                {
                    String subtopicSeparator = destination.getServerSettings().getSubtopicSeparator();
                    Subtopic consumerSubtopic = new Subtopic(csub, subtopicSeparator);
                    Subtopic messageSubtopic = new Subtopic(subtopic, subtopicSeparator);
                    if (!consumerSubtopic.matches(messageSubtopic))
                        continue; // Not a match.
                }                
                
                if (csel == null)  
                    return true;

                JMSSelector selector = new JMSSelector(csel);
                try
                {
                    if (selector.match(message))
                    {
                        return true;
                    }
                }
                catch (JMSSelectorException jmse)
                {
                    // Log a warning for this client's selector and continue
                    if (Log.isWarn())
                    {
                        Log.getLogger(JMSSelector.LOG_CATEGORY).warn("Error processing message selector: " +
                             jmse.toString() + StringUtils.NEWLINE +
                             "  incomingMessage: " + message + StringUtils.NEWLINE +
                             "  selector: " + csel + StringUtils.NEWLINE);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the MessageClient is valid; false if it has been invalidated.
     * 
     * @return true if the MessageClient is valid; otherwise false.
     */
    public boolean isValid()
    {
        synchronized (lock)
        {
            return valid;
        }
    }
    
    /**
     * Invalidates the MessageClient.
     */
    public void invalidate()
    {
        invalidate(false /* don't attempt to notify the client */);
    }
    
    /**
     * Invalidates the MessageClient, and optionally attempts to notify the client that
     * this subscription has been invalidated.
     * This overload is used when a subscription is timed out while the client is still
     * actively connected to the server but should also be used by any custom code on the server
     * that invalidates MessageClients but wishes to notify the client cleanly.
     * 
     * @param notifyClient <code>true</code> to notify the client that its subscription has been
     *                     invalidated.
     */
    public void invalidate(boolean notifyClient)
    {
        synchronized (lock)
        {
            if (!valid || invalidating)
                return; // Already shutting down.
            
            invalidating = true; // This thread gets to shut the MessageClient down.
            cancelTimeout();
        }
        
        // Record whether we're attempting to notify the client or not.
        attemptingInvalidationClientNotification = notifyClient;
        
        // Build a subscription invalidation message and push to the client if it is still valid.
        if (notifyClient && flexClient != null && flexClient.isValid())
        {                
            CommandMessage msg = new CommandMessage();
            msg.setDestination(destination.getId());
            msg.setClientId(clientId);
            msg.setOperation(CommandMessage.SUBSCRIPTION_INVALIDATE_OPERATION);
            Set subscriberIds = new TreeSet();
            subscriberIds.add(clientId);
            try 
            {
                ((MessageService)destination.getService()).pushMessageToClients(destination, subscriberIds, msg, false /* don't eval selector */);
            }
            catch (MessageException ignore) {}
        }        
        
        // Notify messageClientDestroyed listeners that we're being invalidated.
        if (destroyedListeners != null && !destroyedListeners.isEmpty())
        {
            for (Iterator iter = destroyedListeners.iterator(); iter.hasNext();)
            {
                ((MessageClientListener)iter.next()).messageClientDestroyed(this);                
            }
            destroyedListeners.clear();
        }

        // And generate unsubscribe messages for all of the MessageClient's subscriptions and 
        // route them to the destination this MessageClient is subscribed to.
        // The reason we send a message to the service rather than just going straight to the SubscriptionManager
        // is that some adapters manage their own subscription state (i.e. JMS) in addition to us keeping track of 
        // things with our SubscriptionManager.
        ArrayList unsubMessages = new ArrayList();
        synchronized (lock)
        {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext();)
            {
                SubscriptionInfo subInfo = (SubscriptionInfo)iter.next();
                CommandMessage unsubMessage = new CommandMessage();
                unsubMessage.setDestination(destination.getId());
                unsubMessage.setClientId(clientId);
                unsubMessage.setOperation(CommandMessage.UNSUBSCRIBE_OPERATION);
                unsubMessage.setHeader(CommandMessage.SUBSCRIPTION_INVALIDATED_HEADER, Boolean.TRUE);
                unsubMessage.setHeader(CommandMessage.SELECTOR_HEADER, subInfo.selector);
                unsubMessage.setHeader(AsyncMessage.SUBTOPIC_HEADER_NAME, subInfo.subtopic);
                unsubMessages.add(unsubMessage);
            }
        }
        // Release the lock and send the unsub messages.
        for (Iterator iter = unsubMessages.iterator(); iter.hasNext();)
        {
            try
            {
                destination.getService().serviceCommand((CommandMessage)iter.next());
            }
            catch (MessageException me)
            {
                if (Log.isDebug())
                    Log.getLogger(MESSAGE_CLIENT_LOG_CATEGORY).debug("MessageClient: " + getClientId() + " issued an unsubscribe message during invalidation that was not processed but will continue with invalidation. Reason: " + ExceptionUtil.toString(me));
            }
        }
        
        synchronized (lock)
        {
            // If we didn't clean up all subscriptions log an error and continue with shutdown.
            int remainingSubscriptionCount = subscriptions.size();
            if (remainingSubscriptionCount > 0 && Log.isError())
                Log.getLogger(MESSAGE_CLIENT_LOG_CATEGORY).error("MessageClient: " + getClientId() + " failed to remove " + remainingSubscriptionCount + " subscription(s) during invalidation");
        }

        // If someone registered this message client, invalidating it will free
        // their reference which will typically also remove this message client.
        if (registered)
            destination.getSubscriptionManager().releaseMessageClient(this);
            
        synchronized (lock)
        {
            valid = false;
            invalidating = false;
        }
            
        if (Log.isDebug())
            Log.getLogger(MESSAGE_CLIENT_LOG_CATEGORY).debug("MessageClient with clientId '" + clientId + "' for destination '" + destinationId + "' has been invalidated.");
    }      

    /**
     * Pushes the supplied message and then invalidates the MessageClient. 
     * 
     * @param message The message to push to the client before invalidating.
     * When message is null, MessageClient is invalidated silently. 
     */
    public void invalidate(Message message)
    {
        if (message != null)
        {
            message.setDestination(destination.getId());
            message.setClientId(clientId);

            Set subscriberIds = new TreeSet();
            subscriberIds.add(clientId);
            try 
            {
                ((MessageService)destination.getService()).pushMessageToClients(destination, subscriberIds, message, false /* don't eval selector */);
            }
            catch (MessageException ignore) {}
            
            invalidate(true /* attempt to notify remote client */);
        }        
        else
        {
            invalidate();
        }
    }
    
    /**
     * @exclude
     * Compares this MessageClient to the specified object. The result is true if
     * the argument is not null and is a MessageClient instance with a matching 
     * clientId value.
     * 
     * @param o The object to compare this MessageClient to.
     * @return true if the MessageClient is equal; otherwise false.
     */
    @Override public boolean equals(Object o)
    {
        if (o instanceof MessageClient)
        {
            MessageClient c = (MessageClient) o;
            if (c != null && c.getClientId().equals(clientId))
                return true;
        }
        return false;
    }
    
    /**
     * @exclude
     * Returns the hash code for this MessageClient. The returned value is
     * the hash code for the MessageClient's clientId property.
     * 
     * @return The hash code value for this MessageClient.
     */
    @Override public int hashCode() 
    {
        return getClientId().hashCode();
    }       

    /**
     * @exclude
     * The String representation of this MessageClient is returned (its clientId value).
     * 
     * @return The clientId value for this MessageClient.
     */
    @Override public String toString()
    {
        return String.valueOf(clientId);
    }
    
    //----------------------------------
    //  TimeoutAbstractObject overrides
    //----------------------------------     
    
    /**
     * @exclude
     * Implements TimeoutCapable.
     * This method returns the timeout value configured for the MessageClient's destination.
     */
    @Override public long getTimeoutPeriod()
    {
        return destination.getSubscriptionManager().getSubscriptionTimeoutMillis();
    }

    /**
     * @exclude
     * Implements TimeoutCapable.
     * This method is invoked when the MessageClient has timed out and it
     * invalidates the MessageClient.
     */
    @Override public void timeout()
    {
        invalidate(true /* notify client */);
    }
    
    /**
     * @exclude
     * Returns true if a timeout task is running for this MessageClient.
     */
    public boolean isTimingOut()
    {
        return willTimeout;
    }
    
    /**
     * @exclude
     * Records whether a timeout task is running for this MessageClient.
     */
    public void setTimingOut(boolean value)
    {
        willTimeout = value;
    }    

    //--------------------------------------------------------------------------
    //
    // Private Methods
    //
    //--------------------------------------------------------------------------    
    
    /**
     * Utility method that tests validity and throws an exception if the instance
     * has been invalidated.
     */
    private void checkValid()
    {
        synchronized (lock)
        {
            if (!valid)
            {
                throw new RuntimeException("MessageClient has been invalidated."); // TODO - localize
            }
        } 
    }

    //--------------------------------------------------------------------------
    //
    // Nested Classes
    //
    //--------------------------------------------------------------------------    
    
    /**
     * Represents a MessageClient's subscription to a destination.
     * It captures the optional selector expression and subtopic for the
     * subscription.
     */
    protected static class SubscriptionInfo implements Comparable
    {
        public String selector, subtopic;

        SubscriptionInfo(String sel, String sub)
        {
            this.selector = sel;
            this.subtopic = sub;
        }

        @Override public boolean equals(Object o)
        {
            if (o instanceof SubscriptionInfo)
            {
                SubscriptionInfo other = (SubscriptionInfo) o;
                return equalStrings(other.selector, selector) &&
                       equalStrings(other.subtopic, subtopic);
            }
            return false;
        }

        @Override public int hashCode()
        {
            return (selector == null ? 0 : selector.hashCode()) + 
                   (subtopic == null ? 1 : subtopic.hashCode());
        }

        /**
         * Compares the two subscription infos (being careful to
         * ensure we compare in a consistent way if the arguments
         * are switched).
         */
        @Override public int compareTo(Object o)
        {
            SubscriptionInfo other = (SubscriptionInfo) o;
            int result;

            if ((result = compareStrings(other.selector, selector)) != 0) 
                return result;
            else if ((result = compareStrings(other.subtopic, subtopic)) != 0)
                return result;

            return 0;
        }
    }
}
