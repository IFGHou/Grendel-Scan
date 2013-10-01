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

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.FlexSessionListener;
import flex.messaging.MessageClient;
import flex.messaging.MessageClientListener;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.util.TimeoutAbstractObject;
import flex.messaging.util.UUIDUtils;

/**
 * @exclude Instances of this class are used by endpoints that support streaming outbound data to connected clients when the client is not polling and the FlexSession representing the connection does
 *          not support push directly. This generally requires that the client and endpoint establish a separate, physical connection for pushed data that is part of a larger, logical
 *          connection/session.
 *          <p>
 *          When the endpoint establishes this physical streaming connection it will create an instance of this class, register it with the FlexClient and then wait on the public
 *          <code>pushNeeded</code> condition variable. When data arrives to push to the remote client, the FlexClient will queue it with this notifier instance and the waiting endpoint will be
 *          notified. The endpoint will retrieve the queued messages from the notifier instance and will stream them to the client and then go back into a wait state on the <code>pushNeeded</code>
 *          condition variable.
 *          </p>
 *          <p>
 *          Note that this implementation is based upon <code>Object.wait()</code>; it is not a non-blocking implementation.
 *          </p>
 */
public class EndpointPushNotifier extends TimeoutAbstractObject implements EndpointPushHandler, FlexSessionListener, MessageClientListener
{
    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs a PushNotifier instance.
     * 
     * @param endpoint
     *            The endpoint that will use this notifier.
     * @param flexClient
     *            The FlexClient that will use this notifier.
     */
    public EndpointPushNotifier(Endpoint endpoint, FlexClient flexClient)
    {
        notifierId = UUIDUtils.createUUID(false /* doesn't need to be secure */);
        this.endpoint = endpoint;
        this.flexClient = flexClient;
        flexClient.registerEndpointPushHandler(this, endpoint.getId());
        flexSession = FlexContext.getFlexSession();
        if (flexSession != null)
            flexSession.addSessionDestroyedListener(this);
        updateLastUse(); // Initialize last use timestamp to construct time.
    }

    // --------------------------------------------------------------------------
    //
    // Public Variables
    //
    // --------------------------------------------------------------------------

    /**
     * The condition variable that the endpoint waits on for pushed data to arrive.
     */
    public final Object pushNeeded = new Object();

    // --------------------------------------------------------------------------
    //
    // Private Variables
    //
    // --------------------------------------------------------------------------

    /**
     * Flag indicating whether the notifier has been closed/shut down. This is used to signal a waiting endpoint that it should break out of its wait loop and close its streaming connection.
     */
    private volatile boolean closed;

    /**
     * Flag indicating that the notifier has started closing; used to allow only one thread to execute the close() logic and delay flipping closed to true to allow any final messages to be streamed to
     * the client before the endpoint using the notifier breaks out of its wait/notify loop and terminates the streaming connection.
     */
    private volatile boolean closing;

    /**
     * The number of minutes a client can remain idle before the server times the notifier out.
     */
    private int idleTimeoutMinutes;

    /**
     * The endpoint that uses this notifier.
     */
    private final Endpoint endpoint;

    /**
     * The FlexClient this notifier is associated with.
     */
    private final FlexClient flexClient;

    /**
     * The FlexSession this notifier is associated with.
     */
    private final FlexSession flexSession;

    /**
     * Lock for instance-level synchronization.
     */
    private final Object lock = new Object();

    /**
     * Log category used by the notifier. Initialized to ENDPOINT_GENERAL but endpoints using this notifier should set it to their own log categories.
     */
    private String logCategory = LogCategories.ENDPOINT_GENERAL;

    /**
     * Queue of messages that the FlexClient will populate and the endpoint will drain to stream to the client.
     */
    private List messages;

    /**
     * List of MessageClient subscriptions using this endpoint push notifier. When this notifier is closed, any associated subscriptions need to be invalidated.
     */
    private final CopyOnWriteArrayList messageClients = new CopyOnWriteArrayList();

    /**
     * Unique identifier for this instance.
     */
    private final String notifierId;

    // --------------------------------------------------------------------------
    //
    // Public Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Moves this notifier to a closed state, notifying any listeners, associated subscriptions and waiting endpoints. Does not attempt to notify the client Channel of the disconnect thereby allowing
     * automatic reconnect processing to run.
     */
    @Override
    public void close()
    {
        close(false);
    }

    /**
     * Moves this notifier to a closed state, notifying any listeners, associated subscriptions and waiting endpoints. Attempts to notify the client Channel of an explicit disconnect, thereby
     * suppressing automatic reconnect processing.
     * 
     * @param disconnectChannel
     *            True to attempt to notify the client Channel of the disconnect and suppress automatic reconnect processing.
     */
    @Override
    public void close(boolean disconnectChannel)
    {
        synchronized (lock)
        {
            if (closed || closing)
                return;

            closing = true;
        }

        cancelTimeout();

        if (flexSession != null)
            flexSession.removeSessionDestroyedListener(this);

        // Shut down flow of further messages to this notifier.
        flexClient.unregisterEndpointPushHandler(this, endpoint.getId());

        // Push a disconnect command down to the client to suppress automatic reconnect.
        if (disconnectChannel)
        {
            ArrayList<AsyncMessage> list = new ArrayList<AsyncMessage>(1);
            CommandMessage disconnect = new CommandMessage(CommandMessage.DISCONNECT_OPERATION);
            list.add(disconnect);
            pushMessages(list);
        }

        // Invalidate associated subscriptions; this doesn't attempt to notify the client.
        // Any client subscriptions made over this endpoint will be automatically invalidated
        // on the client when it receives its channel disconnect event.
        for (Iterator iter = messageClients.iterator(); iter.hasNext();)
            ((MessageClient) iter.next()).invalidate();

        // Move to final closed state; after this we need to notify one last time to stream
        // any final messages to the client and allow the endpoint to shut down its streaming
        // connection.
        synchronized (lock)
        {
            closed = true;
            closing = false;
        }
        synchronized (pushNeeded)
        {
            pushNeeded.notifyAll();
        }
    }

    /**
     * Returns any messages available to push to the client, and removes them from this notifier. Notified endpoints should invoke this method to retrieve messages, stream them to the client and then
     * re-enter the wait state. This method acquires a lock on <code>pushNeeded</code>.
     * 
     * @return The messages to push to the client.
     */
    public List drainMessages()
    {
        synchronized (pushNeeded)
        {
            List messagesToPush = messages;
            messages = null;
            return messagesToPush;
        }
    }

    /**
     * Returns whether the notifier has closed; used to break the endpoint's wait cycle.
     * 
     * @return True if the notifier has closed; otherwise false.
     */
    public boolean isClosed()
    {
        return closed;
    }

    /**
     * Returns the endpoint that is using this notifier.
     * 
     * @return The endpoint using this notifier.
     */
    public Endpoint getEndpoint()
    {
        return endpoint;
    }

    /**
     * Returns the idle timeout minutes used by the notifier.
     * 
     * @return The idle timeout minutes used by the notifier.
     */
    public int getIdleTimeoutMinutes()
    {
        return idleTimeoutMinutes;
    }

    /**
     * Sets the idle timeout minutes used by the notifier.
     * 
     * @param idleTimeoutMinutes
     *            The idle timeout minutes used by the notifier.
     */
    public void setIdleTimeoutMinutes(int idleTimeoutMinutes)
    {
        this.idleTimeoutMinutes = idleTimeoutMinutes;
    }

    /**
     * Returns the log category used by this notifier.
     * 
     * @return The log category used by this notifier.
     */
    public String getLogCategory()
    {
        return logCategory;
    }

    /**
     * Sets the log category used by this notifier. Endpoints using this notifier should set it to their own log categories.
     * 
     * @param logCategory
     *            The log category for the notifier to use.
     */
    public void setLogCategory(String logCategory)
    {
        this.logCategory = logCategory;
    }

    /**
     * Returns the unique id for this notifier.
     * 
     * @return The unique id for this notifier.
     */
    public String getNotifierId()
    {
        return notifierId;
    }

    /**
     * @exclude Implements TimeoutCapable. Determine the time, in milliseconds, that this object is allowed to idle before having its timeout method invoked.
     */
    @Override
    public long getTimeoutPeriod()
    {
        return (idleTimeoutMinutes * 60 * 1000);
    }

    /**
     * @exclude No-op.
     */
    @Override
    public void messageClientCreated(MessageClient messageClient)
    {
    }

    /**
     * @exclude When a subscription that is receiving pushed data via this notifier is shutdown unregister it from this notifier.
     */
    @Override
    public void messageClientDestroyed(MessageClient messageClient)
    {
        unregisterMessageClient(messageClient);
    }

    /**
     * Used by FlexClient to push messages to the endpoint. This method will automatically notify a waiting endpoint, if one exists and it acquires a lock on <code>pushNeeded</code>.
     * 
     * @param messages
     *            The messages to push to the client.
     */
    @Override
    public void pushMessages(List messagesToPush)
    {
        if (!messagesToPush.isEmpty())
        {
            synchronized (pushNeeded)
            {
                // Push these straight on through; notify immediately.
                if (messages == null)
                    messages = messagesToPush;
                else
                    messages.addAll(messagesToPush);

                // If the notifier isn�t closing, notify; otherwise just add and the close will
                // notify once it completes.
                if (!closing)
                    pushNeeded.notifyAll();
            }
        }
    }

    /**
     * Registers a MessageClient subscription that depends on this notifier.
     * 
     * @param messageClient
     *            A MessageClient that depends on this notifier.
     */
    @Override
    public void registerMessageClient(MessageClient messageClient)
    {
        if (messageClient != null)
        {
            if (messageClients.addIfAbsent(messageClient))
                messageClient.addMessageClientDestroyedListener(this);
        }
    }

    /**
     * Handle session creation events. This handler is a no-op because the notifier is only concerned with its associated session's destruction.
     * 
     * @param flexSession
     *            The newly created FlexSession.
     */
    @Override
    public void sessionCreated(FlexSession flexSession)
    {
        // No-op.
    }

    /**
     * Handle session destruction events. This will be invoked when the notifier's associated session is invalidated, and this will trigger the notifier to close.
     * 
     * @param flexSession
     *            The FlexSession being invalidated.
     */
    @Override
    public void sessionDestroyed(FlexSession flexSession)
    {
        if (Log.isInfo())
            Log.getLogger(logCategory).info("Endpoint with id '" + endpoint.getId() + "' is closing" + " a streaming connection for the FlexClient with id '" + flexClient.getId() + "'" + " since its associated session has been destroyed.");
        close(true /* disconnect client Channel */);
    }

    /**
     * @exclude Implements TimeoutCapable. Inform the object that it has timed out.
     */
    @Override
    public void timeout()
    {
        if (Log.isInfo())
            Log.getLogger(logCategory).info("Endpoint with id '" + endpoint.getId() + "' is timing out" + " a streaming connection for the FlexClient with id '" + flexClient.getId() + "'");
        close(true /* disconnect client Channel */);
    }

    /**
     * Unregisters a MessageClient subscription that depended on this notifier.
     * 
     * @param messageClient
     *            A MessageClient that depended on this notifier.
     */
    @Override
    public void unregisterMessageClient(MessageClient messageClient)
    {
        if (messageClient != null)
        {
            messageClient.removeMessageClientDestroyedListener(this);
            messageClients.remove(messageClient);
        }
    }
}
