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
package flex.messaging;

import flex.management.runtime.messaging.MessageDestinationControl;
import flex.management.runtime.messaging.services.messaging.SubscriptionManagerControl;
import flex.management.runtime.messaging.services.messaging.ThrottleManagerControl;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationException;
import flex.messaging.config.DestinationSettings;
import flex.messaging.config.NetworkSettings;
import flex.messaging.config.ServerSettings;
import flex.messaging.config.ThrottleSettings;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.services.MessageService;
import flex.messaging.services.Service;
import flex.messaging.services.messaging.MessagingConstants;
import flex.messaging.services.messaging.RemoteSubscriptionManager;
import flex.messaging.services.messaging.SubscriptionManager;
import flex.messaging.services.messaging.ThrottleManager;

/**
 * A logical reference to a MessageDestination.
 * 
 * @author neville
 */
public class MessageDestination extends FactoryDestination
{
    static final long serialVersionUID = -2016911808141319012L;

    /** Log category for <code>MessageDestination</code>. */
    public static final String LOG_CATEGORY = LogCategories.SERVICE_MESSAGE;

    // Errors
    private static final int UNSUPPORTED_POLICY = 10124;

    // Destination properties
    private ServerSettings serverSettings;

    // Destination internal
    private SubscriptionManager subscriptionManager;
    private RemoteSubscriptionManager remoteSubscriptionManager;
    private ThrottleManager throttleManager;

    private MessageDestinationControl controller;

    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an unmanaged <code>MessageDestination</code> instance.
     */
    public MessageDestination()
    {
        this(false);
    }

    /**
     * Constructs a <code>MessageDestination</code> with the indicated management.
     * 
     * @param enableManagement
     *            <code>true</code> if the <code>MessageDestination</code> is manageable; otherwise <code>false</code>.
     */
    public MessageDestination(boolean enableManagement)
    {
        super(enableManagement);

        serverSettings = new ServerSettings();

        // Managers
        subscriptionManager = new SubscriptionManager(this);
        remoteSubscriptionManager = new RemoteSubscriptionManager(this);
        throttleManager = new ThrottleManager();
    }

    // --------------------------------------------------------------------------
    //
    // Initialize, validate, start, and stop methods.
    //
    // --------------------------------------------------------------------------

    /**
     * Initializes the <code>MessageDestination</code> with the properties. If subclasses override, they must call <code>super.initialize()</code>.
     * 
     * @param id
     *            The id of the destination.
     * @param properties
     *            Properties for the <code>MessageDestination</code>.
     */
    @Override
    public void initialize(String id, ConfigMap properties)
    {
        super.initialize(id, properties);

        if (properties == null || properties.size() == 0)
            return;

        // Network properties
        network(properties);

        // Server properties
        server(properties);
    }

    /**
     * This method first calls stop on its superclass and then cleans up the SubscriptionManager.
     */
    @Override
    public void stop()
    {
        // Stop all the managers first.
        subscriptionManager.stop();
        remoteSubscriptionManager.stop();
        throttleManager.stop();

        super.stop();
    }

    // --------------------------------------------------------------------------
    //
    // Public Getters and Setters for Destination properties
    //
    // --------------------------------------------------------------------------

    /**
     * Sets the <code>NetworkSettings</code> of the <code>MessageDestination</code>.
     * 
     * @param networkSettings
     *            The <code>NetworkSettings</code> of the <code>MessageDestination</code>
     */
    @Override
    public void setNetworkSettings(NetworkSettings networkSettings)
    {
        super.setNetworkSettings(networkSettings);

        // Set throttle and subscription manager settings if needed
        if (networkSettings.getThrottleSettings() != null)
        {
            ThrottleSettings settings = networkSettings.getThrottleSettings();
            settings.setDestinationName(getId());
            throttleManager.setThrottleSettings(settings);
        }
        if (networkSettings.getSubscriptionTimeoutMinutes() > 0)
        {
            long subscriptionTimeoutMillis = networkSettings.getSubscriptionTimeoutMinutes() * 60 * 1000; // Convert to millis.
            subscriptionManager.setSubscriptionTimeoutMillis(subscriptionTimeoutMillis);
        }
    }

    /**
     * Returns the <code>ServerSettings</code> of the <code>MessageDestination</code>.
     * 
     * @return The <code>ServerSettings</code> of the <code>MessageDestination</code>.
     */
    public ServerSettings getServerSettings()
    {
        return serverSettings;
    }

    /**
     * Sets the <code>ServerSettings</code> of the <code>MessageDestination</code>.
     * 
     * @param serverSettings
     *            The <code>ServerSettings</code> of the <code>MessageDestination</code>
     */
    public void setServerSettings(ServerSettings serverSettings)
    {
        this.serverSettings = serverSettings;
    }

    /**
     * Casts the <code>Service</code> into <code>MessageService</code> and calls super.setService.
     * 
     * @param service
     *            The <code>Service</code> managing this <code>Destination</code>.
     */
    @Override
    public void setService(Service service)
    {
        MessageService messageService = (MessageService) service;
        super.setService(messageService);
    }

    // --------------------------------------------------------------------------
    //
    // Other public APIs
    //
    // --------------------------------------------------------------------------

    /** @exclude */
    public SubscriptionManager getSubscriptionManager()
    {
        return subscriptionManager;
    }

    /** @exclude */
    public RemoteSubscriptionManager getRemoteSubscriptionManager()
    {
        return remoteSubscriptionManager;
    }

    /** @exclude */
    public ThrottleManager getThrottleManager()
    {
        return throttleManager;
    }

    /** @exclude **/
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Destination)
        {
            Destination d = (Destination) o;
            if (d != null && d.getServiceType().equals(getServiceType()) && d.getId().equals(getId()))
            {
                return true;
            }
        }
        return false;
    }

    /** @exclude **/
    @Override
    public int hashCode()
    {
        return (getServiceType() == null ? 0 : getServiceType().hashCode()) * 100003 + (getId() == null ? 0 : getId().hashCode());
    }

    /** @exclude **/
    @Override
    public String toString()
    {
        return getServiceType() + "#" + getId();
    }

    // --------------------------------------------------------------------------
    //
    // Protected/private APIs
    //
    // --------------------------------------------------------------------------

    protected void network(ConfigMap properties)
    {
        ConfigMap network = properties.getPropertyAsMap(NetworkSettings.NETWORK_ELEMENT, null);
        if (network != null)
        {
            // Get implementation specific network settings, including subclasses!
            NetworkSettings ns = getNetworkSettings();

            // Subscriber timeout; first check for subscription-timeout-minutes and fallback to legacy session-timeout.
            int useLegacyPropertyToken = -999999;
            int subscriptionTimeoutMinutes = network.getPropertyAsInt(NetworkSettings.SUBSCRIPTION_TIMEOUT_MINUTES, useLegacyPropertyToken);
            if (subscriptionTimeoutMinutes == useLegacyPropertyToken)
                subscriptionTimeoutMinutes = network.getPropertyAsInt(NetworkSettings.SESSION_TIMEOUT, NetworkSettings.DEFAULT_TIMEOUT);
            ns.setSubscriptionTimeoutMinutes(subscriptionTimeoutMinutes);

            // Throttle Settings
            throttle(ns.getThrottleSettings(), network);

            setNetworkSettings(ns);
        }
    }

    protected void throttle(ThrottleSettings ts, ConfigMap network)
    {
        ConfigMap inbound = network.getPropertyAsMap(ThrottleSettings.ELEMENT_INBOUND, null);
        if (inbound != null)
        {
            int policy = getPolicyFromThrottleSettings(inbound);
            ts.setInboundPolicy(policy);
            int destFreq = inbound.getPropertyAsInt(ThrottleSettings.ELEMENT_DEST_FREQ, 0);
            ts.setIncomingDestinationFrequency(destFreq);
            int clientFreq = inbound.getPropertyAsInt(ThrottleSettings.ELEMENT_CLIENT_FREQ, 0);
            ts.setIncomingClientFrequency(clientFreq);
        }

        ConfigMap outbound = network.getPropertyAsMap(ThrottleSettings.ELEMENT_OUTBOUND, null);
        if (outbound != null)
        {
            int policy = getPolicyFromThrottleSettings(outbound);
            ts.setOutboundPolicy(policy);
            int destFreq = outbound.getPropertyAsInt(ThrottleSettings.ELEMENT_DEST_FREQ, 0);
            ts.setOutgoingDestinationFrequency(destFreq);
            int clientFreq = outbound.getPropertyAsInt(ThrottleSettings.ELEMENT_CLIENT_FREQ, 0);
            ts.setOutgoingClientFrequency(clientFreq);
        }
    }

    private int getPolicyFromThrottleSettings(ConfigMap settings)
    {
        String policyString = settings.getPropertyAsString(ThrottleSettings.ELEMENT_POLICY, null);
        int policy = ThrottleSettings.POLICY_NONE;
        if (policyString == null)
            return policy;
        try
        {
            policy = ThrottleSettings.parsePolicy(policyString);
            if (policy == ThrottleSettings.POLICY_REPLACE)
            {
                if (Log.isWarn())
                {
                    Log.getLogger(getLogCategory()).warn("Throttle outbound policy '{0}' found on message destination '{1}'." + " The '{0}' throttle outbound policy has been deprecated. Please remove it from your configuration file.",
                                    new Object[] { "REPLACE", id });
                }
            }
        }
        catch (ConfigurationException exception)
        {
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(UNSUPPORTED_POLICY, new Object[] { getId(), policyString });
            throw ce;
        }
        return policy;
    }

    protected void server(ConfigMap properties)
    {
        ConfigMap server = properties.getPropertyAsMap(DestinationSettings.SERVER_ELEMENT, null);
        if (server != null)
        {
            int max = server.getPropertyAsInt(MessagingConstants.MAX_CACHE_SIZE_ELEMENT, MessagingConstants.DEFAULT_MAX_CACHE_SIZE);
            serverSettings.setMaxCacheSize(max);

            long ttl = server.getPropertyAsLong(MessagingConstants.TIME_TO_LIVE_ELEMENT, -1);
            serverSettings.setMessageTTL(ttl);

            boolean durable = server.getPropertyAsBoolean(MessagingConstants.IS_DURABLE_ELEMENT, false);
            serverSettings.setDurable(durable);

            boolean allowSubtopics = server.getPropertyAsBoolean(MessagingConstants.ALLOW_SUBTOPICS_ELEMENT, false);
            serverSettings.setAllowSubtopics(allowSubtopics);

            String subtopicSeparator = server.getPropertyAsString(MessagingConstants.SUBTOPIC_SEPARATOR_ELEMENT, MessagingConstants.DEFAULT_SUBTOPIC_SEPARATOR);
            serverSettings.setSubtopicSeparator(subtopicSeparator);

            String routingMode = server.getPropertyAsString(MessagingConstants.CLUSTER_MESSAGE_ROUTING, "server-to-server");
            serverSettings.setBroadcastRoutingMode(routingMode);
        }
    }

    /**
     * Returns the log category of the <code>MessageDestination</code>.
     * 
     * @return The log category of the component.
     */
    @Override
    protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Invoked automatically to allow the <code>MessageDestination</code> to setup its corresponding MBean control.
     * 
     * @param service
     *            The <code>Service</code> that manages this <code>MessageDestination</code>.
     */
    @Override
    protected void setupDestinationControl(Service service)
    {
        controller = new MessageDestinationControl(this, service.getControl());
        controller.register();
        setControl(controller);
        setupThrottleManagerControl(controller);
        setupSubscriptionManagerControl(controller);
    }

    private void setupThrottleManagerControl(MessageDestinationControl destinationControl)
    {
        ThrottleManagerControl throttleManagerControl = new ThrottleManagerControl(getThrottleManager(), destinationControl);
        throttleManagerControl.register();
        getThrottleManager().setControl(throttleManagerControl);
        getThrottleManager().setManaged(true);
        destinationControl.setThrottleManager(throttleManagerControl.getObjectName());
    }

    private void setupSubscriptionManagerControl(MessageDestinationControl destinationControl)
    {
        SubscriptionManagerControl subscriptionManagerControl = new SubscriptionManagerControl(getSubscriptionManager(), destinationControl);
        subscriptionManagerControl.register();
        getSubscriptionManager().setControl(subscriptionManagerControl);
        getSubscriptionManager().setManaged(true);
        destinationControl.setSubscriptionManager(subscriptionManagerControl.getObjectName());
    }
}
