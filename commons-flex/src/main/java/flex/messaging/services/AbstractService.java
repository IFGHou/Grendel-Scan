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
package flex.messaging.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import flex.management.ManageableComponent;
import flex.management.runtime.messaging.MessageBrokerControl;
import flex.messaging.Destination;
import flex.messaging.MessageBroker;
import flex.messaging.MessageException;
import flex.messaging.cluster.ClusterManager;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

/**
 * This is the default implementation of <code>Service</code>, which provides a convenient base for behavior and associations common to all Services.
 * 
 * @author neville
 */
public abstract class AbstractService extends ManageableComponent implements Service
{
    /** Log category for <code>AbstractService</code>. */
    public static final String LOG_CATEGORY = LogCategories.SERVICE_GENERAL;
    /**
     * Log category that captures startup information for service's destinations.
     */
    public static final String LOG_CATEGORY_STARTUP_DESTINATION = LogCategories.STARTUP_DESTINATION;

    // Errors
    protected static final int UNKNOWN_MESSAGE_TYPE = 10454;

    // AbstractService's properties
    protected Map adapterClasses;
    protected String defaultAdapterId;
    protected List defaultChannels;
    protected Map destinations;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>AbstractService</code>.
     */
    public AbstractService()
    {
        this(false);
    }

    /**
     * Constructs an <code>AbstractService</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>AbstractService</code> is manageable; otherwise <code>false</code>.
     */
    public AbstractService(boolean enableManagement)
    {
        super(enableManagement);

        adapterClasses = new HashMap();
        destinations = new ConcurrentHashMap();
    }

    // --------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    // --------------------------------------------------------------------------

    /**
     * Verifies that the <code>AbstractService</code> is in valid state before it is started. If subclasses override, they must call <code>super.validate()</code>.
     */
    @Override
    protected void validate()
    {
        if (isValid())
            return;

        super.validate();

        if (defaultChannels != null)
        {
            for (Iterator iter = defaultChannels.iterator(); iter.hasNext();)
            {
                String id = (String) iter.next();
                if (!getMessageBroker().getChannelIds().contains(id))
                {
                    iter.remove();
                    if (Log.isWarn())
                    {
                        Log.getLogger(getLogCategory()).warn("Removing the Channel " + id + " from Destination " + getId() + "as MessageBroker does not know the channel");
                    }
                }
            }
        }
        else
        {
            defaultChannels = getMessageBroker().getDefaultChannels();
        }
    }

    /**
     * Starts the service if its associated <code>MessageBroker</code> is started. and if the service is not already running. The default implementation of this method starts all of the destinations
     * of the service. If subclasses override, they must call <code>super.start()</code>.
     */
    @Override
    public void start()
    {
        if (isStarted())
        {
            // Needed for destinations added after startup.
            startDestinations();
            return;
        }

        // Check if the MessageBroker is started
        MessageBroker broker = getMessageBroker();
        if (!broker.isStarted())
        {
            if (Log.isWarn())
            {
                Log.getLogger(getLogCategory()).warn("Service with id '{0}' cannot be started" + " when the MessageBroker is not started.", new Object[] { getId() });
            }
            return;
        }

        // Set up management
        if (isManaged() && broker.isManaged())
        {
            setupServiceControl(broker);
            MessageBrokerControl controller = (MessageBrokerControl) broker.getControl();
            if (getControl() != null)
                controller.addService(getControl().getObjectName());
        }

        super.start();

        startDestinations();
    }

    /**
     * The default implementation of this method stops all of the destinations of the service. If subclasses override, they must call <code>super.stop()</code>.
     */
    @Override
    public void stop()
    {
        if (!isStarted())
        {
            return;
        }

        stopDestinations();

        super.stop();

        // Remove management
        if (isManaged() && getMessageBroker().isManaged())
        {
            if (getControl() != null)
            {
                getControl().unregister();
                setControl(null);
            }
            setManaged(false);
        }
    }

    // --------------------------------------------------------------------------
    //
    // Public Getters and Setters for AbstractService properties
    //
    // --------------------------------------------------------------------------

    /**
     * Returns the adapters registered with the <code>AbstractService</code>.
     * 
     * @return The Map of adapter id and classes.
     */
    @Override
    public Map getRegisteredAdapters()
    {
        return adapterClasses;
    }

    /**
     * Registers the adapter with the <code>AbstractService</code>.
     * 
     * @param id
     *            The id of the adapter.
     * @param adapterClass
     *            The class of the adapter.
     * @return The previous adapter class that the id was associated with.
     */
    @Override
    public String registerAdapter(String id, String adapterClass)
    {
        return (String) adapterClasses.put(id, adapterClass);
    }

    /**
     * Unregistered the adapter with the <code>AbstractService</code> and set the default adapter to <code>null</code> if needed.
     * 
     * @param id
     *            The id of the adapter.
     * @return The adapter class that the id was associated with.
     */
    @Override
    public String unregisterAdapter(String id)
    {
        if (id != null && id.equals(defaultAdapterId))
            defaultAdapterId = null;

        return (String) adapterClasses.remove(id);

    }

    /**
     * Returns the id of the default adapter of the <code>AbstractService</code>.
     * 
     * @return defaultAdapterId The id of the default adapter of the <code>AbstractService</code>.
     */
    @Override
    public String getDefaultAdapter()
    {
        return defaultAdapterId;
    }

    /**
     * Sets the default adapter of the <code>AbstractService</code>.
     * 
     * @param id
     *            The id of the default adapter.
     */
    @Override
    public void setDefaultAdapter(String id)
    {
        if (adapterClasses.get(id) == null)
        {
            // No adapter with id '{0}' is registered with the service '{1}'.
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.UNREGISTERED_ADAPTER, new Object[] { id, getId() });
            throw ex;
        }
        defaultAdapterId = id;
    }

    /**
     * Returns the list of channel ids of the <code>AbstractService</code>.
     */
    @Override
    public List getDefaultChannels()
    {
        return defaultChannels;
    }

    /**
     * Adds the channel to the list of channels of the <code>AbstractService</code>. <code>MessageBroker</code> has to know the channel. Otherwise, the channel is not added to the list.
     * 
     * @param id
     *            The id of the channel.
     */
    @Override
    public void addDefaultChannel(String id)
    {
        if (defaultChannels == null)
            defaultChannels = new ArrayList();
        else if (defaultChannels.contains(id))
            return;

        if (isStarted())
        {
            List channelIds = getMessageBroker().getChannelIds();
            if (channelIds == null || !channelIds.contains(id))
            {
                // No channel with id ''{0}'' is known by the MessageBroker.
                if (Log.isWarn())
                {
                    Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker." + " Not adding the channel.", new Object[] { id });
                }
                return;
            }
        }
        // Either message broker knows about the channel, or service is not
        // running and channel will be checked during startup
        defaultChannels.add(id);
    }

    /**
     * Sets the channel list of the <code>AbstractService</code>. <code>MessageBroker</code> has to know the channels, otherwise they are not added to the list.
     * 
     * @param ids
     *            List of channel ids.
     */
    @Override
    public void setDefaultChannels(List ids)
    {
        if (ids != null && isStarted())
        {
            List channelIds = getMessageBroker().getChannelIds();
            for (Iterator iter = ids.iterator(); iter.hasNext();)
            {
                String id = (String) iter.next();
                if (channelIds == null || !channelIds.contains(id))
                {
                    iter.remove();
                    if (Log.isWarn())
                    {
                        Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker." + " Not adding the channel.", new Object[] { id });
                    }
                }
            }
        }
        // Otherwise, channels will be checked before startup
        defaultChannels = ids;
    }

    /**
     * Removes the channel from the list of channels for the <code>AbstractService</code>.
     * 
     * @param id
     *            The id of the channel.
     * @return <code>true</code> if the list contained the channel id.
     */
    @Override
    public boolean removeDefaultChannel(String id)
    {
        if (defaultChannels == null)
            return false;
        return defaultChannels.remove(id);
    }

    /**
     * Returns the <code>Destination</code> that the <code>Message</code> targets.
     * 
     * @return The <code>Destination</code> that the <code>Message</code> targets.
     * @throws <code>MessageException</code> if no such <code>Destination</code> exists.
     */
    @Override
    public Destination getDestination(Message message)
    {
        String id = message.getDestination();
        Destination result = getDestination(id);
        if (result == null)
        {
            throw new MessageException("No destination '" + id + "' exists in service " + getClass().getName());
        }
        return result;
    }

    /**
     * Returns the <code>Destination</code> with the specified id or null if no <code>Destination</code> with id exists.
     * 
     * @param id
     *            The id of the <code>Destination</code>.
     */
    @Override
    public Destination getDestination(String id)
    {
        Destination result = (Destination) destinations.get(id);
        return result;
    }

    /**
     * Returns the Map of <code>Destination</code> ids and instances.
     * 
     * @return The Map of <code>Destination</code> ids and instances.
     */
    @Override
    public Map getDestinations()
    {
        return destinations;
    }

    /**
     * Creates a <code>Destination</code> instance, sets its id, sets it manageable if the <code>AbstractService</code> that created it is manageable, and sets its <code>Service</code> to the
     * <code>AbstractService</code> that created it.
     * 
     * @param id
     *            The id of the <code>Destination</code>.
     * @return The <code>Destination</code> instanced created.
     */
    @Override
    public Destination createDestination(String id)
    {
        Destination destination = new Destination();
        destination.setId(id);
        destination.setManaged(isManaged());
        destination.setService(this);

        return destination;
    }

    /**
     * Adds the <code>Destination</code> instance to the list of destinations known by the <code>AbstractService</code>. It also sets destination's service to this <code>AbstractService</code>
     * instance. Note that <code>Destination</code> cannot be null, it cannot have a null id, and it cannot have an id of a <code>Destination</code> already registered with the
     * <code>AbstractService</code>.
     * 
     * <code>Destination</code> needs to be started if the <code>AbstractService</code> is already running.
     * 
     * @param destination
     *            The <code>Destination</code> instance to be added.
     */
    @Override
    public void addDestination(Destination destination)
    {
        if (destination == null)
        {
            // Cannot add null ''{0}'' to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT, new Object[] { "Destination", "Service" });
            throw ex;
        }

        String id = destination.getId();

        if (id == null)
        {
            // Cannot add ''{0}'' with null id to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT_ID, new Object[] { "Destination", "Service" });
            throw ex;
        }
        // No need to add if the destination is already there
        if (getDestination(id) == destination)
        {
            return;
        }

        // Register with the MessageBroker first to make sure no destination
        // with the same id exists in another service.
        getMessageBroker().registerDestination(id, getId());

        destinations.put(id, destination);

        if (destination.getService() == null || destination.getService() != this)
        {
            destination.setService(this);
        }
    }

    /**
     * Removes the <code>Destination</code> from the list of destinations known by the <code>AbstractService</code>.
     * 
     * @param id
     *            The id of the <code>Destination</code>.
     * @return Previous <code>Destination</code> associated with the id.
     */
    @Override
    public Destination removeDestination(String id)
    {
        Destination destination = (Destination) destinations.get(id);
        if (destination != null)
        {
            destination.stop();
            destinations.remove(id);
            getMessageBroker().unregisterDestination(id);
        }
        return destination;
    }

    /**
     * Sets the id of the <code>AbstractService</code>. If the <code>AbstractService</code> has a <code>MessageBroker</code> assigned, it also updates the id in the <code>MessageBroker</code>.
     */
    @Override
    public void setId(String id)
    {
        String oldId = getId();

        super.setId(id);

        // Update the service id in the broker
        MessageBroker broker = getMessageBroker();
        if (broker != null)
        {
            // broker must have the service then
            broker.removeService(oldId);
            broker.addService(this);
        }
    }

    /**
     * Returns the <code>MessageBroker</code> of the <code>AbstractService</code>.
     * 
     * @return MessageBroker of the <code>AbstractService</code>.
     */
    @Override
    public MessageBroker getMessageBroker()
    {
        return (MessageBroker) getParent();
    }

    /**
     * Sets the <code>MessageBroker</code> of the <code>AbstractService</code>. Removes the <code>AbstractService</code> from the old broker (if there was one) and adds to the list of services in the
     * new broker.
     * 
     * @param broker
     *            <code>MessageBroker</code> of the <code>AbstractService</code>.
     */
    @Override
    public void setMessageBroker(MessageBroker broker)
    {
        MessageBroker oldBroker = getMessageBroker();

        setParent(broker);

        if (oldBroker != null)
        {
            oldBroker.removeService(getId());
        }

        // Add service to the new broker if needed
        if (broker.getService(getId()) != this)
            broker.addService(this);
    }

    // --------------------------------------------------------------------------
    //
    // Other Public APIs
    //
    // --------------------------------------------------------------------------

    /**
     * Returns a <code>ConfigMap</code> service properties that the client needs. By default, it returns null. Subclasses can override to return properties relevant to their implementation.
     * 
     * @param endpoint
     *            Endpoint used to filter the destinations of the service.
     * @return ConfigMap of service properties.
     */
    @Override
    public ConfigMap describeService(Endpoint endpoint)
    {
        return null;
    }

    /**
     * Processes messages by invoking the requested destination's adapter. Subclasses should provide their implementation.
     */
    @Override
    public abstract Object serviceMessage(Message message);

    @Override
    public Object serviceCommand(CommandMessage message)
    {
        Object result = serviceCommonCommands(message);
        if (result != null)
        {
            // TODO: ServiceControl needs this method.
            /*
             * if (isManaged()) { ((ServiceControl)getControl()).incrementServiceCommandCount(); }
             */
            return result;
        }
        throw new MessageException("Service Does Not Support Command Type " + message.getOperation());
    }

    // --------------------------------------------------------------------------
    //
    // Protected/private methods.
    //
    // --------------------------------------------------------------------------

    protected Object serviceCommonCommands(CommandMessage message)
    {
        Object commandResult = null;
        if (message.getOperation() == CommandMessage.CLIENT_PING_OPERATION)
        {
            commandResult = Boolean.TRUE;
        }
        else if (message.getOperation() == CommandMessage.CLUSTER_REQUEST_OPERATION)
        {
            ClusterManager clusterManager = getMessageBroker().getClusterManager();
            String serviceType = getClass().getName();
            String destinationName = message.getDestination();
            if (clusterManager.isDestinationClustered(serviceType, destinationName))
            {
                commandResult = clusterManager.getEndpointsForDestination(serviceType, destinationName);
            }
            else
            {
                // client should never send this message if its local
                // config declares the destination is not clustered
                commandResult = Boolean.FALSE;
            }
        }
        return commandResult;
    }

    /**
     * Returns the log category of the <code>AbstractService</code>. Subclasses can override to provide a more specific logging category.
     * 
     * @return The log category.
     */
    @Override
    protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Invoked automatically to allow the <code>AbstractService</code> to setup its corresponding MBean control. Subclasses should override to setup and register their MBean control. Manageable
     * subclasses should override this template method.
     * 
     * @param broker
     *            The <code>MessageBroker</code> that manages this <code>AbstractService</code>.
     */
    protected abstract void setupServiceControl(MessageBroker broker);

    /**
     * Start all of the destinations of the service.
     */
    private void startDestinations()
    {
        for (Iterator iter = destinations.values().iterator(); iter.hasNext();)
        {
            Destination destination = (Destination) iter.next();

            long timeBeforeStartup = 0;
            if (Log.isDebug())
                timeBeforeStartup = System.currentTimeMillis();

            destination.start();

            if (Log.isDebug())
            {
                long timeAfterStartup = System.currentTimeMillis();
                Long diffMillis = new Long(timeAfterStartup - timeBeforeStartup);
                Log.getLogger(LOG_CATEGORY_STARTUP_DESTINATION).debug("Destination with id '{0}' is ready (startup time: '{1}' ms)", new Object[] { destination.getId(), diffMillis });
            }
        }
    }

    /**
     * Stop all of the destinations of the service.
     */
    private void stopDestinations()
    {
        for (Iterator iter = destinations.values().iterator(); iter.hasNext();)
        {
            Destination destination = (Destination) iter.next();
            destination.stop();
        }
    }
}
