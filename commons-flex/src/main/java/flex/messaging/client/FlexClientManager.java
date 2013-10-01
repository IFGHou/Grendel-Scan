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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import flex.management.ManageableComponent;
import flex.management.runtime.messaging.client.FlexClientManagerControl;
import flex.messaging.MessageBroker;
import flex.messaging.config.FlexClientSettings;
import flex.messaging.endpoints.AbstractEndpoint;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.util.ClassUtil;
import flex.messaging.util.TimeoutAbstractObject;
import flex.messaging.util.TimeoutManager;

/**
 * @exclude Manages FlexClient instances for a MessageBroker.
 */
public class FlexClientManager extends ManageableComponent
{
    public static final String TYPE = "FlexClientManager";

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * @exclude
     */
    public FlexClientManager()
    {
        this(MessageBroker.getMessageBroker(null));
    }

    /**
     * Constructs a FlexClientManager for the passed MessageBroker.
     */
    public FlexClientManager(MessageBroker broker)
    {
        this(false, broker);
    }

    /**
     * @exclude
     */
    public FlexClientManager(boolean enableManagement, MessageBroker mbroker)
    {
        super(enableManagement);

        super.setId(TYPE);

        // Ensure that we have a message broker:
        broker = (mbroker != null) ? mbroker : MessageBroker.getMessageBroker(null);

        FlexClientSettings flexClientSettings = broker.getFlexClientSettings();
        if (flexClientSettings != null)
        {
            // Convert from minutes to millis.
            setFlexClientTimeoutMillis(flexClientSettings.getTimeoutMinutes() * 60 * 1000);
        }

        this.setParent(broker);
    }

    // --------------------------------------------------------------------------
    //
    // Variables
    //
    // --------------------------------------------------------------------------

    /**
     * The MessageBroker that owns this manager.
     */
    private final MessageBroker broker;

    /**
     * The Mbean controller for this manager.
     */
    private FlexClientManagerControl controller;

    /**
     * Table to store FlexClients by id.
     */
    private final Map flexClients = new ConcurrentHashMap();

    /**
     * A Timer to use to schedule delayed flushes of outbound messages for FlexClients.
     */
    private Timer flushScheduler;
    private final Object flushInitLock = new Object();

    /**
     * Manages time outs for FlexClients. This currently includes timeout of FlexClient instances as well as timeouts for async long-poll handling.
     */
    private volatile TimeoutManager flexClientTimeoutManager;

    // --------------------------------------------------------------------------
    //
    // Properties
    //
    // --------------------------------------------------------------------------

    // ----------------------------------
    // clientIds
    // ----------------------------------

    /**
     * A string array of the client IDs.
     */
    public String[] getClientIds()
    {
        String[] ids = new String[flexClients.size()];
        ArrayList idList = new ArrayList(flexClients.keySet());

        for (int i = 0; i < flexClients.size(); i++)
        {
            ids[i] = (String) (idList).get(i);
        }

        return ids;
    }

    // ----------------------------------
    // flexClientCount
    // ----------------------------------

    /**
     * The number of FlexClients in use.
     */
    public int getFlexClientCount()
    {
        return flexClients.size();
    }

    // ----------------------------------
    // flexClientTimeoutMillis
    // ----------------------------------

    private volatile long flexClientTimeoutMillis;

    /**
     * The idle timeout in milliseconds to apply to new FlexClient instances.
     */
    public long getFlexClientTimeoutMillis()
    {
        return flexClientTimeoutMillis;
    }

    /**
     * Sets the idle timeout in milliseconds to apply to new FlexClient instances.
     * 
     * @param value
     *            The idle timeout in milliseconds to apply to new FlexClient instances.
     */
    public void setFlexClientTimeoutMillis(long value)
    {
        if (value < 1)
            value = 0;

        synchronized (this)
        {
            flexClientTimeoutMillis = value;
        }
    }

    // ----------------------------------
    // messageBroker
    // ----------------------------------

    /**
     * Returns the MessageBroker instance that owns this FlexClientManager.
     * 
     * @return The parent MessageBroker instance.
     */
    public MessageBroker getMessageBroker()
    {
        return broker;
    }

    // --------------------------------------------------------------------------
    //
    // Public Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Factory method to create new FlexClients with the specified id.
     */
    public FlexClient getFlexClient(String id)
    {
        FlexClient flexClient = null;
        // Try to lookup an existing instance if we receive an id.
        if (id != null)
        {
            flexClient = (FlexClient) flexClients.get(id);
            if (flexClient != null)
            {
                if (flexClient.isValid() && !flexClient.invalidating)
                {
                    flexClient.updateLastUse();
                    return flexClient;
                }
                else
                // Invalid, remove it - it will be replaced below.
                {
                    flexClients.remove(id);
                }
            }
        }
        // Use a manager-level lock (this) when creating/recreating a new FlexClient.
        synchronized (this)
        {
            if (id != null)
            {
                flexClient = (FlexClient) flexClients.get(id);
                if (flexClient != null)
                {
                    flexClient.updateLastUse();
                    return flexClient;
                }
                else
                {
                    flexClient = new FlexClient(this, id);
                }
            }
            else
            {
                flexClient = new FlexClient(this);
            }
            flexClients.put(flexClient.getId(), flexClient);
            if (flexClientTimeoutMillis > 0)
                flexClientTimeoutManager.scheduleTimeout(flexClient);
            flexClient.notifyCreated();
            return flexClient;
        }
    }

    /**
     * Creates a FlexClientOutboundQueueProcessor instance and hooks it up to the passed FlexClient.
     * 
     * @param client
     *            The FlexClient to equip with a queue processor.
     * @param endpointId
     *            The Id of the endpoint the queue processor is used for.
     * @return The FlexClient with a configured queue processor.
     */
    public FlexClientOutboundQueueProcessor createOutboundQueueProcessor(FlexClient flexClient, String endpointId)
    {
        FlexClientOutboundQueueProcessor processor = null;

        try
        {
            Endpoint endpoint = broker.getEndpoint(endpointId);
            if (endpoint instanceof AbstractEndpoint)
            {
                Class processorClass = ((AbstractEndpoint) endpoint).getFlexClientOutboundQueueProcessorClass();
                if (processorClass != null)
                {
                    Object instance = ClassUtil.createDefaultInstance(processorClass, null);
                    if (instance instanceof FlexClientOutboundQueueProcessor)
                    {
                        processor = (FlexClientOutboundQueueProcessor) instance;
                        processor.setFlexClient(flexClient);
                        processor.setEndpointId(endpointId);
                        processor.initialize(((AbstractEndpoint) endpoint).getFlexClientOutboundQueueProcessorConfig());
                    }
                }
            }
        }
        catch (Throwable t)
        {
            if (Log.isWarn())
                Log.getLogger(FlexClient.FLEX_CLIENT_LOG_CATEGORY).warn("Failed to create custom outbound queue processor for FlexClient with id '" + flexClient.getId() + "'. Using default queue processor.", t);
        }

        if (processor == null)
        {
            processor = new FlexClientOutboundQueueProcessor();
            processor.setFlexClient(flexClient);
            processor.setEndpointId(endpointId);
        }

        return processor;
    }

    /**
     * @exclude Monitors an async poll for a FlexClient for timeout.
     * 
     * @param asyncPollTimeout
     *            The async poll task to monitor for timeout.
     */
    public void monitorAsyncPollTimeout(TimeoutAbstractObject asyncPollTimeout)
    {
        flexClientTimeoutManager.scheduleTimeout(asyncPollTimeout);
    }

    /**
     * @exclude Schedules a timed flush for the passed FlexClient to be invoked in the future.
     * 
     * @param flexClient
     *            The FlexClient to flush.
     */
    public void scheduleFlush(TimerTask flushTask, int waitInterval)
    {
        synchronized (flushInitLock)
        {
            if (flushScheduler == null)
                flushScheduler = new Timer(true /* make this a daemon so it shuts down quickly */);
        }

        flushScheduler.schedule(flushTask, waitInterval);
    }

    /**
     * @see flex.management.ManageableComponent#start()
     */
    @Override
    public void start()
    {
        if (isManaged())
        {
            controller = new FlexClientManagerControl(getParent().getControl(), this);
            setControl(controller);
            controller.register();
        }

        final String baseId = getId();
        flexClientTimeoutManager = new TimeoutManager(new ThreadFactory()
        {
            int counter = 1;

            @Override
            public synchronized Thread newThread(Runnable runnable)
            {
                Thread t = new Thread(runnable);
                t.setName(baseId + "-TimeoutThread-" + counter++);
                return t;
            }
        });
    }

    /**
     * @see flex.management.ManageableComponent#stop()
     */
    @Override
    public void stop()
    {
        if (controller != null)
        {
            controller.unregister();
        }

        if (flushScheduler != null)
            flushScheduler.cancel();

        if (flexClientTimeoutManager != null)
            flexClientTimeoutManager.shutdown();
    }

    // --------------------------------------------------------------------------
    //
    // Protected Methods
    //
    // --------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.ManageableComponent#getLogCategory()
     */
    @Override
    protected String getLogCategory()
    {
        return LogCategories.CLIENT_FLEXCLIENT;
    }

    // --------------------------------------------------------------------------
    //
    // Package Private Methods
    //
    // --------------------------------------------------------------------------

    /**
     * @exclude Removes a FlexClient from being managed by this manager. This method is invoked by FlexClients when they are invalidated.
     * 
     * @param The
     *            id of the FlexClient being invalidated.
     */
    void removeFlexClient(FlexClient flexClient)
    {
        if (flexClient != null)
        {
            String id = flexClient.getId();
            synchronized (id)
            {
                FlexClient storedClient = (FlexClient) flexClients.get(id);
                // If the stored instance is the same as the invalidating instance based upon identity,
                // remove it.
                if (storedClient == flexClient)
                    flexClients.remove(id);
            }
        }
    }
}
