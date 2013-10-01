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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import flex.management.ManageableComponent;
import flex.management.runtime.messaging.MessageBrokerControl;
import flex.management.runtime.messaging.log.LogManager;
import flex.messaging.client.FlexClient;
import flex.messaging.client.FlexClientManager;
import flex.messaging.cluster.ClusterManager;
import flex.messaging.config.ChannelSettings;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.ConfigurationException;
import flex.messaging.config.ConfigurationManager;
import flex.messaging.config.FlexClientSettings;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.config.SecuritySettings;
import flex.messaging.config.SystemSettings;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.factories.JavaFactory;
import flex.messaging.log.Log;
import flex.messaging.log.LogCategories;
import flex.messaging.messages.AbstractMessage;
import flex.messaging.messages.AcknowledgeMessage;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.security.LoginManager;
import flex.messaging.security.SecurityException;
import flex.messaging.services.Service;
import flex.messaging.services.ServiceException;
import flex.messaging.util.Base64;
import flex.messaging.util.ClassUtil;
import flex.messaging.util.ExceptionUtil;
import flex.messaging.util.RedeployManager;
import flex.messaging.util.StringUtils;

/**
 * The MessageBroker is the hub of message traffic in the Flex system. It has a number of endpoints which send and receive messages over the network, and it has a number of services that are message
 * destinations. The broker routes decoded messages received by endpoints to services based on the service destination specified in each message. The broker also has a means of pushing messages back
 * through endpoints to clients.
 */
public final class MessageBroker extends ManageableComponent
{
    /** Log category for <code>MessageBroker</code>. **/
    public static final String LOG_CATEGORY = LogCategories.MESSAGE_GENERAL;

    /**
     * Log category that captures startup information for broker's destinations.
     */
    public static final String LOG_CATEGORY_STARTUP_SERVICE = LogCategories.STARTUP_SERVICE;

    /** @exclude */
    public static final String TYPE = "MessageBroker";

    /** @exclude */
    private static final String LOG_MANAGER_ID = "log";

    /** @exclude */
    private static final int NULL_ENDPOINT_URL = 10128;

    /** @exclude */
    private static final int SERVICE_TYPE_EXISTS = 11113;

    /** @exclude */
    private static final int NO_SERVICE_FOR_DEST = 10004;

    /** @exclude */
    private static final int SERVICE_CMD_NOT_SUPPORTED = 10451;

    /** @exclude */
    private static final int DESTINATION_UNACCESSIBLE = 10005;

    /** @exclude */
    private static final int UNKNOWN_REMOTE_CREDENTIALS_FORMAT = 10020;

    /** @exclude */
    private static final int URI_ALREADY_REGISTERED = 11109;

    /** @exclude */
    private static final int NULL_MESSAGE_ID = 10029;

    /** @exclude */
    private static final Integer INTEGER_ONE = new Integer(1);

    /** @exclude */
    private InternalPathResolver internalPathResolver;

    /** @exclude */
    private Map attributes;

    /** @exclude */
    private Map endpoints;

    /** @exclude */
    private Map services;

    /** @exclude */
    private Map servers;

    /** @exclude */
    private Map factories;

    /** @exclude */
    private Map registeredEndpoints;

    /** @exclude */
    private ClusterManager clusterManager;

    /** @exclude */
    private Map destinationToService; // destinationId ==> serviceId mapping

    /** @exclude */
    private FlexClientManager flexClientManager;

    /** @exclude */
    private LoginManager loginManager;

    /** @exclude */
    private RedeployManager redeployManager;

    /** @exclude */
    private Map channelSettings;

    /** @exclude */
    private List defaultChannels;

    /** @exclude */
    private SecuritySettings securitySettings;

    /** @exclude */
    private SessionMetricsTracker sessionMetricsTracker;

    /** @exclude */
    private FlexClientSettings flexClientSettings;

    /** @exclude */
    private static ThreadLocal systemSettingsThreadLocal = new ThreadLocal();

    /** @exclude */
    private SystemSettings systemSettings;

    /** @exclude */
    private ServletContext initServletContext;

    /** @exclude */
    private final ConcurrentHashMap serviceValidationListeners = new ConcurrentHashMap();

    /** @exclude */
    private ClassLoader classLoader;

    /** @exclude */
    private Log log;

    /** @exclude */
    private LogManager logManager;

    /** The default message broker id when one is not specified in web.xml. */
    static final String DEFAULT_BROKER_ID = "__default__";

    /** A map of currently available message brokers indexed by message broker id. */
    static Map messageBrokers = new HashMap();

    /** @exclude */
    private MessageBrokerControl controller;

    /**
     * map of attribute ids of Application or Session level scoped destination assemblers to the number of active destinations referring to
     * 
     * @exclude
     */
    private Map attributeIdRefCounts = new HashMap();

    /**
     * Create a MessageBroker. This constructor will establish collections for routers, endpoints, and services.
     * 
     * @exclude
     */
    public MessageBroker()
    {
        this(true, null);
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public MessageBroker(boolean enableManagement)
    // {
    // this(enableManagement, null);
    // }

    /** @exclude */
    public MessageBroker(boolean enableManagement, String mbid)
    {
        this(enableManagement, mbid, MessageBroker.class.getClassLoader());
    }

    /** @exclude */
    public MessageBroker(boolean enableManagement, String mbid, ClassLoader loader)
    {
        super(enableManagement);
        classLoader = loader;
        attributes = new ConcurrentHashMap();
        destinationToService = new HashMap();
        endpoints = new LinkedHashMap();
        services = new LinkedHashMap();
        servers = new LinkedHashMap();
        factories = new HashMap();
        registeredEndpoints = new HashMap();

        // Add the built-in java factory
        addFactory("java", new JavaFactory());

        setId(mbid);

        log = Log.createLog();

        clusterManager = new ClusterManager(this);
        systemSettings = new SystemSettings();
        systemSettingsThreadLocal.set(systemSettings);
        clusterManager = new ClusterManager(this);
        sessionMetricsTracker = new SessionMetricsTracker(this);

        if (isManaged())
        {
            controller = new MessageBrokerControl(this);
            controller.register();
            setControl(controller);

            logManager = new LogManager();
            logManager.setLog(log);
            logManager.setParent(this);
            logManager.setupLogControl();
            logManager.initialize(LOG_MANAGER_ID, null);
        }
        // Create the FlexClientManager after MessageBrokerControl is created.
        flexClientManager = new FlexClientManager(isManaged(), this);
    }

    /**
     * Sets the id of the <code>MessageBroker</code>. If id is null, uses the default broker id.
     * 
     * @exclude
     */
    @Override
    public void setId(String id)
    {
        if (id == null)
        {
            id = DEFAULT_BROKER_ID;
        }
        super.setId(id);
    }

    /**
     * Retrieves a message broker with the supplied id. This is defined via the servlet init parameter messageBrokerId. If no messageBrokerId is supplied, pass in a null value for the id parameter.
     * 
     * @param id
     *            The id of the message broker to retrieve.
     * @return The <code>MessageBroker</code> for the supplied id.
     */
    public static MessageBroker getMessageBroker(String id)
    {
        if (id == null)
            id = DEFAULT_BROKER_ID;
        return (MessageBroker) messageBrokers.get(id);
    }

    /**
     * Start the message broker's endpoints and services.
     * 
     * @exclude
     */
    @Override
    public void start()
    {
        if (isStarted())
            return;

        /*
         * J2EE can be a real pain in terms of getting the right class loader so dump out some detailed info about what is going on.
         */
        if (Log.isDebug())
        {
            StringBuffer sb = new StringBuffer();
            if (classLoader == MessageBroker.class.getClassLoader())
                sb.append(" the MessageBroker's class loader");
            if (classLoader == Thread.currentThread().getContextClassLoader())
            {
                if (sb.length() > 0)
                    sb.append(" and");
                sb.append(" the context class loader");
            }
            if (sb.length() == 0)
                sb.append(" not the context or the message broker's class loader");
            Log.getLogger(LogCategories.CONFIGURATION).debug("MessageBroker id: " + getId() + " classLoader is:" + sb.toString() + " (" + "classLoader " + ClassUtil.classLoaderToString(classLoader));
        }

        // Catch any startup errors and log using our log machinery, then rethrow to trigger shutdown.
        try
        {
            // MessageBroker doesn't call super.start() because it doesn't need the
            // usual validation that other components need
            setStarted(true);

            registerMessageBroker();
            sessionMetricsTracker.start();
            flexClientManager.start();
            startServices();
            loginManager.start();
            startEndpoints();
            startServers();
            redeployManager.start();
        }
        catch (Exception e)
        {
            if (Log.isError())
                Log.getLogger(LogCategories.CONFIGURATION).error("MessageBroker failed to start: " + ExceptionUtil.exceptionFollowedByRootCausesToString(e));

            // Rethrow.
            RuntimeException re = new RuntimeException(e.getMessage(), e);
            throw re;
        }
    }

    /**
     * Stop the broker's endpoints, clusters, and services.
     * 
     * @exclude
     */
    @Override
    public void stop()
    {
        if (!isStarted())
            return;

        if (Log.isDebug())
            Log.getLogger(LogCategories.CONFIGURATION).debug("MessageBroker stopping: " + getId());

        serviceValidationListeners.clear();

        sessionMetricsTracker.stop();
        flexClientManager.stop();
        stopServers();
        stopEndpoints();

        // set this MB in FlexContext as it is needed for reference counts in destination stopping
        FlexContext.setThreadLocalObjects(null, null, this);
        stopServices();
        FlexContext.setThreadLocalObjects(null, null, null);

        if (loginManager != null)
            loginManager.stop();
        try
        {
            if (redeployManager != null)
                redeployManager.stop();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        clusterManager.destroyClusters();

        super.stop();
        unRegisterMessageBroker();

        // clear system settings
        systemSettings.clear();
        systemSettings = null;
        systemSettingsThreadLocal.remove();

        if (Log.isDebug())
            Log.getLogger(LogCategories.CONFIGURATION).debug("MessageBroker stopped: " + getId());
    }

    /**
     * Returns an <tt>Iterator</tt> containing the current names that attributes have been bound to the <tt>MessageBroker</tt> under. Use {@link #getAttribute(String)} to retrieve an attribute value.
     * 
     * @return An iterator containing the current names of the attributes.
     */
    public Iterator getAttributeNames()
    {
        return attributes.keySet().iterator();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Returns the attribute value bound to the <tt>MessageBroker</tt> under the provided name.
    // *
    // * @param name The attribute name.
    // */
    // public Object getAttribute(String name)
    // {
    // return attributes.get(name);
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Binds an attribute value to the <tt>MessageBroker</tt> under the provided name.
    // *
    // * @param name The attribute name.
    // * @param value The attribute value.
    // */
    // public void setAttribute(String name, Object value)
    // {
    // if (value == null)
    // removeAttribute(name);
    // else
    // attributes.put(name, value);
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Removes the attribute with the given name from the <tt>MessageBroker</tt>.
    // *
    // * @param name The attribute name.
    // */
    // public void removeAttribute(String name)
    // {
    // attributes.remove(name);
    // }

    /** @exclude */
    public void setInternalPathResolver(InternalPathResolver internalPathResolver)
    {
        this.internalPathResolver = internalPathResolver;
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public InputStream resolveInternalPath(String filename) throws IOException
    // {
    // return internalPathResolver.resolve(filename);
    // }

    /** @exclude */
    public interface InternalPathResolver
    {
        InputStream resolve(String filename) throws IOException;
    }

    /** @exclude */
    public ClusterManager getClusterManager()
    {
        return clusterManager;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Add a <code>Server</code> to the broker's collection.
    // *
    // * @param server <code>Server</code> to be added.
    // */
    // public void addServer(Server server)
    // {
    // if (server == null)
    // {
    // // Cannot add null ''{0}'' to the ''{1}''
    // ConfigurationException ex = new ConfigurationException();
    // ex.setMessage(ConfigurationConstants.NULL_COMPONENT, new Object[]{"Server", "MessageBroker"});
    // throw ex;
    // }
    //
    // String id = server.getId();
    //
    // if (id == null)
    // {
    // // Cannot add ''{0}'' with null id to the ''{1}''
    // ConfigurationException ex = new ConfigurationException();
    // ex.setMessage(ConfigurationConstants.NULL_COMPONENT_ID, new Object[]{"Server", "MessageBroker"});
    // throw ex;
    // }
    //
    // // No need to add if server is already added
    // Server currentServer = getServer(id);
    // if (currentServer == server)
    // return;
    //
    // // Do not allow servers with the same id
    // if (currentServer != null)
    // {
    // // Cannot add a ''{0}'' with the id ''{1}'' that is already registered with the ''{2}''
    // ConfigurationException ex = new ConfigurationException();
    // ex.setMessage(ConfigurationConstants.DUPLICATE_COMPONENT_ID, new Object[]{"Server", id, "MessageBroker"});
    // throw ex;
    // }
    //
    // servers.put(id, server);
    // }

    /**
     * @exclude Returns the <code>Server</code> with the specified id.
     * 
     * @param id
     *            The id of the <code>Server</code>/
     * @return The <code>Server</code> with the specified id or null if no <code>Server</code> with the id exists.
     */
    public Server getServer(String id)
    {
        return (Server) servers.get(id);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Stops and removes the <code>Server</code> from the set of shared servers managed by the <code>MessageBroker</code>.
    // *
    // * @param id The id of the <code>Server</code> to remove.
    // * @return <code>Server</code> that has been removed or <code>null</code> if it doesn't exist.
    // */
    // public Server removeServer(String id)
    // {
    // Server server = (Server)servers.get(id);
    // if (server != null)
    // {
    // server.stop();
    // servers.remove(id);
    // }
    // return server;
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Creates an <code>Endpoint</code> instance, sets its id and url.
    // * It further sets the endpoint manageable if the <code>MessageBroker</code>
    // * is manageable, and assigns its <code>MessageBroker</code> to the
    // * <code>MessageBroker</code> that created it.
    // *
    // * @param id The id of the endpoint.
    // * @param url The url of the endpoint.
    // * @param className The class name of the endpoint.
    // *
    // * @return The created <code>Endpoint</code> instance.
    // */
    // public Endpoint createEndpoint(String id, String url, String className)
    // {
    // Class endpointClass = ClassUtil.createClass(className, getClassLoader());
    //
    // Endpoint endpoint = (Endpoint)ClassUtil.createDefaultInstance(endpointClass, Endpoint.class);
    // endpoint.setId(id);
    // endpoint.setUrl(url);
    // endpoint.setManaged(isManaged());
    // endpoint.setMessageBroker(this);
    //
    // return endpoint;
    // }

    /**
     * @exclude Add an endpoint to the broker's collection. Broker will accept the endpoint to be added only if the endpoint is not null, it does not have null id or url, and it does not have the same
     *          id or url as another endpoint.
     * 
     * @param endpoint
     *            Endpoint to be added.
     */
    public void addEndpoint(Endpoint endpoint)
    {
        if (endpoint == null)
        {
            // Cannot add null ''{0}'' to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT, new Object[] { "Endpoint", "MessageBroker" });
            throw ex;
        }

        String id = endpoint.getId();

        if (id == null)
        {
            // Cannot add ''{0}'' with null id to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT_ID, new Object[] { "Endpoint", "MessageBroker" });
            throw ex;
        }

        // No need to add if endpoint is already added
        if (getEndpoint(id) == endpoint)
            return;

        // Do not allow endpoints with the same id
        if (getEndpoint(id) != null)
        {
            // Cannot add a ''{0}'' with the id ''{1}'' that is already registered with the ''{2}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.DUPLICATE_COMPONENT_ID, new Object[] { "Endpoint", id, "MessageBroker" });
            throw ex;
        }

        // Add the endpoint only if its url is not null and it is not registered
        // by another channel
        checkEndpointUrl(id, endpoint.getUrl());

        // Finally add the endpoint to endpoints map
        endpoints.put(id, endpoint);
    }

    /**
     * @exclude
     */
    private void checkEndpointUrl(String id, String endpointUrl)
    {
        // Do not allow endpoints with null url property.
        if (endpointUrl == null)
        {
            // Cannot add ''{0}'' with null url to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(NULL_ENDPOINT_URL, new Object[] { "Endpoint", "MessageBroker" });
            throw ex;
        }

        String parsedEndpointURI = ChannelSettings.removeTokens(endpointUrl);

        // first check the original URI
        if (registeredEndpoints.containsKey(parsedEndpointURI))
        {
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(URI_ALREADY_REGISTERED, new Object[] { id, parsedEndpointURI, registeredEndpoints.get(parsedEndpointURI) });
            throw ce;
        }

        // add the original URI to the registered endpoints map
        registeredEndpoints.put(parsedEndpointURI, id);

        // also need to check the URI without the context root
        int nextSlash = parsedEndpointURI.indexOf('/', 1);
        if (nextSlash > 0)
        {
            String parsedEndpointURI2 = parsedEndpointURI.substring(nextSlash);
            if (registeredEndpoints.containsKey(parsedEndpointURI2))
            {
                ConfigurationException ce = new ConfigurationException();
                ce.setMessage(URI_ALREADY_REGISTERED, new Object[] { parsedEndpointURI2, id, registeredEndpoints.get(parsedEndpointURI2) });
                throw ce;
            }
            registeredEndpoints.put(parsedEndpointURI2, id);
        }
    }

    /**
     * @exclude Returns the <code>Endpoint</code> with the specified id.
     * 
     * @param id
     *            The id of the <code>Endpoint</code>/
     * @return The <code>Endpoint</code> with the specified id or null if no <code>Endpoint</code> with the id exists.
     */
    public Endpoint getEndpoint(String id)
    {
        return (Endpoint) endpoints.get(id);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Retrieve the map of all endpoints in this broker.
    // */
    // public Map getEndpoints()
    // {
    // return endpoints;
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Retrieve an endpoint based on a requested URL path. Two endpoints should not be
    // * registered to the same path.
    // */
    // public Endpoint getEndpoint(String path, String contextPath)
    // {
    // for (Iterator iter = endpoints.keySet().iterator(); iter.hasNext();)
    // {
    // Object id = iter.next();
    // Endpoint e = (Endpoint)endpoints.get(id);
    //
    // if (matchEndpoint(path, contextPath, e))
    // return e;
    // }
    // MessageException lme = new MessageException();
    // lme.setMessage(10003, new Object[] {path});
    // throw lme;
    // }

    /**
     * @exclude Removes an endpoint from the <code>MessageBroker</code>.
     * 
     * @param id
     *            The id of the endpoint.
     * @return The removed endpoint.
     */
    public Endpoint removeEndpoint(String id)
    {
        Endpoint endpoint = getEndpoint(id);
        if (endpoint != null)
        {
            endpoint.stop();
            endpoints.remove(id);
        }
        return endpoint;
    }

    /**
     * @exclude Matches the current &quot;servlet + pathinfo&quot; to a list of channels registered in the services configuration file, independent of context root.
     * 
     * @param path
     *            The Servlet mapping and PathInfo of the current request
     * @param contextPath
     *            The web application context root (or empty string for default root)
     * @param endpoint
     *            The endpoint to be matched
     * @return whether the current request matches a registered endpoint URI
     * 
     */
    private boolean matchEndpoint(String path, String contextPath, Endpoint endpoint)
    {
        boolean match = false;
        String channelEndpoint = endpoint.getParsedUrl(contextPath);

        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }

        if (path.equalsIgnoreCase(channelEndpoint))
        {
            match = true;
        }

        return match;
    }

    /**
     * Returns the <code>FlexFactory</code> with the specified id.
     * 
     * @param id
     *            The id of the <code>FlexFactory</code>.
     * @return The <code>FlexFactory</code> with the specified id or null if no factory with the id exists.
     */
    public FlexFactory getFactory(String id)
    {
        return (FlexFactory) factories.get(id);
    }

    /**
     * Returns the map of <code>FlexFactory</code> instances.
     * 
     * @return The map of <code>FlexFactory</code> instances.
     */
    public Map getFactories()
    {
        return factories;
    }

    /**
     * Registers a factory with the <code>MessageBroker</code>.
     * 
     * @param id
     *            The id of the factory.
     * @param factory
     *            <code>FlexFactory</code> instance.
     */
    public void addFactory(String id, FlexFactory factory)
    {
        if (id == null)
        {
            // Cannot add ''{0}'' with null id to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT_ID, new Object[] { "FlexFactory", "MessageBroker" });
            throw ex;
        }
        // No need to add if factory is already added
        if (getFactory(id) == factory)
        {
            return;
        }
        // Do not allow multiple factories with the same id
        if (getFactory(id) != null)
        {
            // Cannot add a ''{0}'' with the id ''{1}'' that is already registered with the ''{2}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.DUPLICATE_COMPONENT_ID, new Object[] { "FlexFactory", id, "MessageBroker" });
            throw ex;
        }
        factories.put(id, factory);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Removes the <code>FlexFactory</code> from the list of factories known
    // * by the <code>MessageBroker</code>.
    // *
    // * @param id The id of the <code>FlexFactory</code>.
    // * @return <code>FlexFactory</code> that has been removed.
    // */
    // public FlexFactory removeFactory(String id)
    // {
    // FlexFactory factory = getFactory(id);
    // if (factory != null)
    // {
    // factories.remove(id);
    // }
    // return factory;
    // }

    /**
     * Returns the <code>Service</code> with the specified id.
     * 
     * @param id
     *            The id of the <code>Service</code>/
     * @return The <code>Service</code> with the specified id or null if no <code>Service</code> with the id exists.
     */
    public Service getService(String id)
    {
        return (Service) services.get(id);
    }

    /** @exclude */
    public Service getServiceByType(String type)
    {
        for (Iterator serviceIter = services.values().iterator(); serviceIter.hasNext();)
        {
            Service svc = (Service) serviceIter.next();
            if (svc.getClass().getName().equals(type))
            {
                return svc;
            }
        }
        return null;
    }

    /**
     * Returns the Map of <code>Service</code> instances.
     * 
     * @return The Map of <code>Service</code> instances.
     */
    public Map getServices()
    {
        return services;
    }

    /**
     * Describe services and the channels used by the services for the client.
     * 
     * @param endpoint
     *            Endpoint used to filter the destinations of the service.
     * @return ConfigMap of server properties.
     */
    public ConfigMap describeServices(Endpoint endpoint)
    {
        if (!serviceValidationListeners.isEmpty())
        {
            for (Enumeration iter = serviceValidationListeners.elements(); iter.hasMoreElements();)
            {
                ((ServiceValidationListener) iter.nextElement()).validateServices();
            }
        }

        ConfigMap servicesConfig = new ConfigMap();

        // Keep track of channel ids as we encounter them so we can generate
        // the channel properties that might be needed by the client.
        ArrayList channelIds = new ArrayList();
        channelIds.add(endpoint.getId());

        if (defaultChannels != null)
        {
            ConfigMap defaultChannelsMap = new ConfigMap();
            for (Iterator iter = defaultChannels.iterator(); iter.hasNext();)
            {
                String id = (String) iter.next();
                ConfigMap channelConfig = new ConfigMap();
                channelConfig.addProperty("ref", id);
                defaultChannelsMap.addProperty("channel", channelConfig);
                if (!channelIds.contains(id))
                    channelIds.add(id);
            }
            if (defaultChannelsMap.size() > 0)
                servicesConfig.addProperty("default-channels", defaultChannelsMap);
        }

        for (Iterator iter = services.values().iterator(); iter.hasNext();)
        {
            Service service = (Service) iter.next();
            ConfigMap serviceConfig = service.describeService(endpoint);
            if (serviceConfig != null && serviceConfig.size() > 0)
                servicesConfig.addProperty("service", serviceConfig);
        }

        // Need to send channel properties again in case the client didn't
        // compile in services-config.xml and hence doesn't have channels section
        // of the configuration.
        ConfigMap channels = new ConfigMap();
        for (Iterator iter = channelIds.iterator(); iter.hasNext();)
        {
            String id = (String) iter.next();
            Endpoint currentEndpoint = getEndpoint(id);

            ConfigMap channel = currentEndpoint.describeEndpoint();
            if (channel.size() > 0)
                channels.addProperty("channel", channel);
        }
        if (channels.size() > 0)
            servicesConfig.addProperty("channels", channels);

        if (Log.isDebug())
            Log.getLogger(ConfigurationManager.LOG_CATEGORY).debug("Returning service description for endpoint: " + endpoint + " config: " + servicesConfig);
        return servicesConfig;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Add a listener for the describeServices callback. The describeServices listener
    // * is called before any execution of the describeServices method.
    // *
    // * @param id Identifier of the listener to add
    // * @param listener The listener callback
    // */
    // public void addServiceValidationListener(String id, ServiceValidationListener listener)
    // {
    // if (listener != null)
    // {
    // serviceValidationListeners.putIfAbsent(id, listener);
    // }
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Remove a listener from the describeServices callback.
    // *
    // * @param id Identifier of the listener to remove
    // */
    // public void removeServiceValidationListener(String id)
    // {
    // if (serviceValidationListeners.containsKey(id))
    // {
    // serviceValidationListeners.remove(id);
    // }
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Creates a <code>Service</code> instance, sets its id, sets it manageable
    // * if the <code>MessageBroker</code> that created it is manageable,
    // * and sets its <code>MessageBroker</code> to the <code>MessageBroker</code> that
    // * created it.
    // *
    // * @param id The id of the <code>Service</code>.
    // * @param className The class name of the <code>Service</code>.
    // *
    // * @return The <code>Service</code> instanced created.
    // */
    // public Service createService(String id, String className)
    // {
    // Class svcClass = ClassUtil.createClass(className, getClassLoader());
    //
    // Service service = (Service)ClassUtil.createDefaultInstance(svcClass, Service.class);
    // service.setId(id);
    // service.setManaged(isManaged());
    // service.setMessageBroker(this);
    //
    // return service;
    // }

    /**
     * Add a message type -to- service mapping to the broker's collection. When the broker attempts to route a message to a service, it finds the first service capable of handling the message type.
     * 
     * Note that <code>Service</code> cannot be null, it cannot have a null id, and it cannot have the same id or type of a <code>Service</code> already registered with the <code>MessageBroker</code>.
     * 
     * <code>Service</code> needs to be started if the <code>MessageBroker</code> is already running.
     * 
     * @param service
     *            The service instance used to handle the messages
     * 
     */
    public void addService(Service service)
    {
        if (service == null)
        {
            // Cannot add null ''{0}'' to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT, new Object[] { "Service", "MessageBroker" });
            throw ex;
        }

        String id = service.getId();

        if (id == null)
        {
            // Cannot add ''{0}'' with null id to the ''{1}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.NULL_COMPONENT_ID, new Object[] { "Service", "MessageBroker" });
            throw ex;
        }
        // No need to add if service is already added
        if (getService(id) == service)
        {
            return;
        }
        // Do not allow multiple services with the same id
        if (getService(id) != null)
        {
            // Cannot add a ''{0}'' with the id ''{1}'' that is already registered with the ''{2}''
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.DUPLICATE_COMPONENT_ID, new Object[] { "Service", id, "MessageBroker" });
            throw ex;
        }
        // Do not allow multiple services of the same type
        String type = service.getClass().getName();
        if (getServiceByType(type) != null)
        {
            ConfigurationException ce = new ConfigurationException();
            ce.setMessage(SERVICE_TYPE_EXISTS, new Object[] { type });
            throw ce;
        }

        services.put(id, service);

        if (service.getMessageBroker() == null || service.getMessageBroker() != this)
        {
            service.setMessageBroker(this);
        }
    }

    /**
     * Removes the <code>Service</code> from the list of services known by the <code>MessageBroker</code>.
     * 
     * @param id
     *            The id of the <code>Service</code>.
     * @return Previous <code>Service</code> associated with the id.
     */
    public Service removeService(String id)
    {
        Service service = getService(id);
        if (service != null)
        {
            service.stop();
            services.remove(id);
        }
        return service;
    }

    /**
     * Returns the logger of the <code>MessageBroker</code>.
     * 
     * @return Logger of the <code>MessageBroker</code>.
     */
    public Log getLog()
    {
        return log;
    }

    /** @exclude */
    public LogManager getLogManager()
    {
        return logManager;
    }

    /** @exclude */
    public LoginManager getLoginManager()
    {
        return loginManager;
    }

    /** @exclude */
    public void setLoginManager(LoginManager loginManager)
    {
        if (this.loginManager != null && this.loginManager.isStarted())
            this.loginManager.stop();

        this.loginManager = loginManager;

        if (isStarted())
            loginManager.start();
    }

    /** @exclude */
    public FlexClientManager getFlexClientManager()
    {
        return flexClientManager;
    }

    /** @exclude */
    public void setFlexClientManager(FlexClientManager value)
    {
        flexClientManager = value;
    }

    /** @exclude **/
    public RedeployManager getRedeployManager()
    {
        return redeployManager;
    }

    /** @exclude */
    public void setRedeployManager(RedeployManager redeployManager)
    {
        if (this.redeployManager != null && this.redeployManager.isStarted())
            this.redeployManager.stop();

        this.redeployManager = redeployManager;

        if (isStarted())
            redeployManager.start();
    }

    /**
     * Returns the list of channel ids known to the <code>MessageBroker</code>.
     * 
     * @return The list of channel ids.
     */
    public List getChannelIds()
    {
        return (endpoints == null || endpoints.size() == 0) ? null : new ArrayList(endpoints.keySet());
    }

    /** @exclude */
    public ChannelSettings getChannelSettings(String ref)
    {
        return (ChannelSettings) channelSettings.get(ref);
    }

    /** @exclude */
    public Map getAllChannelSettings()
    {
        return channelSettings;
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public void setChannelSettings(Map channelSettings)
    // {
    // this.channelSettings = channelSettings;
    // }

    /**
     * Returns the default channel ids of the MessageBroker. If a service specifies its own list of channels it overrides these defaults.
     * 
     * @return Default channel ids of the MessageBroker.
     */
    public List getDefaultChannels()
    {
        return defaultChannels;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Adds the channel id to the list of default channel ids.
    // *
    // * @param id The id of the channel to add to the list of default channel ids.
    // */
    // public void addDefaultChannel(String id)
    // {
    // if (defaultChannels == null)
    // defaultChannels = new ArrayList();
    // else if (defaultChannels.contains(id))
    // return;
    //
    // List channelIds = getChannelIds();
    // if (channelIds == null || !channelIds.contains(id))
    // {
    // // No channel with id ''{0}'' is known by the MessageBroker.
    // if (Log.isWarn())
    // {
    // Log.getLogger(LOG_CATEGORY).warn("No channel with id '{0}' is known by the MessageBroker." +
    // " Not adding the channel.",
    // new Object[]{id});
    // }
    // return;
    // }
    // defaultChannels.add(id);
    // }

    /**
     * Sets the default channel ids of the MessageBroker.
     * 
     * @param ids
     *            Default channel ids of the MessageBroker.
     */
    public void setDefaultChannels(List ids)
    {
        if (ids != null)
        {
            List channelIds = getChannelIds();
            for (Iterator iter = ids.iterator(); iter.hasNext();)
            {
                String id = (String) iter.next();
                if (channelIds == null || !channelIds.contains(id))
                {
                    iter.remove();
                    if (Log.isWarn())
                    {
                        Log.getLogger(LOG_CATEGORY).warn("No channel with id '{0}' is known by the MessageBroker." + " Not adding the channel.", new Object[] { id });
                    }
                }
            }
        }
        defaultChannels = ids;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Removes the channel id from the list of default channel ids.
    // *
    // * @param id The id of the channel to remove from the list of default channel ids.
    // * @return <code>true</code> if the list contained the channel id.
    // */
    // public boolean removeDefaultChannel(String id)
    // {
    // if (defaultChannels == null)
    // return false;
    // return defaultChannels.remove(id);
    // }

    /**
     * Returns the <code>SecurityConstraint</code> with the indicated reference id.
     * 
     * @param ref
     *            The reference of the <code>SecurityConstraint</code>
     * @return The <code>SecurityConstraint</code> with the indicated reference id.
     */
    public SecurityConstraint getSecurityConstraint(String ref)
    {
        return getSecuritySettings().getConstraint(ref);
    }

    /** @exclude */
    public ServletContext getInitServletContext()
    {
        return initServletContext;
    }

    /** @exclude */
    protected void setInitServletContext(ServletContext initServletContext)
    {
        this.initServletContext = initServletContext;
    }

    /** @exclude */
    public SecuritySettings getSecuritySettings()
    {
        return securitySettings;
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public void setSecuritySettings(SecuritySettings securitySettings)
    // {
    // this.securitySettings = securitySettings;
    // }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public SystemSettings getLocalSystemSettings()
    // {
    // return systemSettings;
    // }

    /** @exclude */
    public static SystemSettings getSystemSettings()
    {
        SystemSettings ss = (SystemSettings) systemSettingsThreadLocal.get();
        if (ss == null)
        {
            ss = new SystemSettings();
            systemSettingsThreadLocal.set(ss);
        }
        return ss;
    }

    /** @exclude */
    public void setSystemSettings(SystemSettings l)
    {
        if (l != null)
        {
            systemSettingsThreadLocal.set(l);
            systemSettings = l;
        }
    }

    /** @exclude */
    public void clearSystemSettingsThreadLocal()
    {
        systemSettingsThreadLocal.remove();
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public static void releaseThreadLocalObjects()
    // {
    // systemSettingsThreadLocal = null;
    // }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public static void createThreadLocalObjects()
    // {
    // if (systemSettingsThreadLocal == null)
    // systemSettingsThreadLocal = new ThreadLocal();
    // }

    /** @exclude */
    public FlexClientSettings getFlexClientSettings()
    {
        return flexClientSettings;
    }

    /** @exclude */
    public void setFlexClientSettings(FlexClientSettings value)
    {
        flexClientSettings = value;
    }

    // TODO UCdetector: Remove unused code:
    // /** @exclude */
    // public void initThreadLocals()
    // {
    // // Update thread locals
    // setSystemSettings(systemSettings);
    // }

    /**
     * Start all of the broker's shared servers.
     */
    private void startServers()
    {
        for (Iterator iter = servers.entrySet().iterator(); iter.hasNext();)
        {
            Server server = (Server) ((Entry) iter.next()).getValue();
            server.start();
        }
    }

    /**
     * Stop all the broker's shared servers.
     */
    private void stopServers()
    {
        for (Iterator iter = servers.entrySet().iterator(); iter.hasNext();)
        {
            Server server = (Server) ((Entry) iter.next()).getValue();
            server.stop();
        }
    }

    /**
     * Start all of the broker's endpoints.
     * 
     * @exclude
     */
    private void startEndpoints()
    {
        for (Iterator iter = endpoints.values().iterator(); iter.hasNext();)
        {
            Endpoint endpoint = (Endpoint) iter.next();
            endpoint.start();
        }
    }

    /**
     * Stop all of the broker's endpoints.
     * 
     * @exclude
     */
    private void stopEndpoints()
    {
        for (Iterator iter = endpoints.values().iterator(); iter.hasNext();)
        {
            Endpoint endpoint = (Endpoint) iter.next();
            endpoint.stop();
        }
    }

    /**
     * Start all of the broker's services.
     * 
     * @exclude
     */
    private void startServices()
    {
        for (Iterator iter = services.values().iterator(); iter.hasNext();)
        {
            Service svc = (Service) iter.next();

            long timeBeforeStartup = 0;
            if (Log.isDebug())
            {
                timeBeforeStartup = System.currentTimeMillis();
                Log.getLogger(LOG_CATEGORY_STARTUP_SERVICE).debug("Service with id '{0}' is starting.", new Object[] { svc.getId() });
            }

            svc.start();

            if (Log.isDebug())
            {
                long timeAfterStartup = System.currentTimeMillis();
                Long diffMillis = new Long(timeAfterStartup - timeBeforeStartup);
                Log.getLogger(LOG_CATEGORY_STARTUP_SERVICE).debug("Service with id '{0}' is ready (startup time: '{1}' ms)", new Object[] { svc.getId(), diffMillis });
            }
        }
    }

    /**
     * Stop all of the broker's services.
     * 
     * @exclude
     */
    private void stopServices()
    {
        for (Iterator iter = services.values().iterator(); iter.hasNext();)
        {
            Service svc = (Service) iter.next();
            svc.stop();
        }
    }

    /**
     * You can call this method in order to send a message from your code into the message routing system. The message is routed to a service that is defined to handle messages of this type. Once the
     * service is identified, the destination property of the message is used to find a destination configured for that service. The adapter defined for that destination is used to handle the message.
     * 
     * @param message
     *            The message to be routed to a service
     * @param endpoint
     *            This can identify the endpoint that is sending the message but it is currently not used so you may pass in null.
     * @return <code>AcknowledgeMessage</code> with result.
     */
    public AcknowledgeMessage routeMessageToService(Message message, Endpoint endpoint)
    {
        // Make sure message has a messageId
        checkMessageId(message);

        Object serviceResult = null;
        boolean serviced = false;
        Service service = null;
        String destId = message.getDestination();
        try
        {
            String serviceId = (String) destinationToService.get(destId);

            if ((serviceId == null) && (destId != null) && (!serviceValidationListeners.isEmpty()))
            {
                for (Enumeration iter = serviceValidationListeners.elements(); iter.hasMoreElements();)
                {
                    ((ServiceValidationListener) iter.nextElement()).validateDestination(destId);
                }
                serviceId = (String) destinationToService.get(destId);
            }

            if (serviceId != null)
            {
                service = (Service) services.get(serviceId);
                serviced = true;
                Destination destination = service.getDestination(destId);
                inspectOperation(message, destination);
                // Remove the validate endopint header if it was set.
                if (message.headerExists(Message.VALIDATE_ENDPOINT_HEADER))
                    message.getHeaders().remove(Message.VALIDATE_ENDPOINT_HEADER);

                if (Log.isDebug())
                    Log.getLogger(getLogCategory(message)).debug("Before invoke service: " + service.getId() + StringUtils.NEWLINE + "  incomingMessage: " + message + StringUtils.NEWLINE);

                extractRemoteCredentials(service, message);
                serviceResult = service.serviceMessage(message);
            }

            if (!serviced)
            {
                MessageException lme = new MessageException();
                // No destination with id ''{0}'' is registered with any service.
                lme.setMessage(NO_SERVICE_FOR_DEST, new Object[] { destId });
                throw lme;
            }

            if (Log.isDebug())
            {
                String debugServiceResult = Log.getPrettyPrinter().prettify(serviceResult);
                Log.getLogger(getLogCategory(message)).debug("After invoke service: " + service.getId() + StringUtils.NEWLINE + "  reply: " + debugServiceResult + StringUtils.NEWLINE);
            }

            AcknowledgeMessage ack = null;
            if (serviceResult instanceof AcknowledgeMessage)
            {
                // service will return an ack if they need to transform it in some
                // service-specific way (paging is an example)
                ack = (AcknowledgeMessage) serviceResult;
            }
            else
            {
                // most services will return a result of some sort, possibly null,
                // and expect the broker to compose a message to deliver it
                ack = new AcknowledgeMessage();
                ack.setBody(serviceResult);
            }
            ack.setCorrelationId(message.getMessageId());
            ack.setClientId(message.getClientId());
            return ack;
        }
        catch (MessageException exc)
        {
            exc.logAtHingePoint(message, null, /* No outbound error message at this point. */
                            "Exception when invoking service '" + (service == null ? "(none)" : service.getId()) + "': ");

            throw exc;
        }
        catch (RuntimeException exc)
        {
            Log.getLogger(LogCategories.MESSAGE_GENERAL).error(
                            "Exception when invoking service: " + (service == null ? "(none)" : service.getId()) + StringUtils.NEWLINE + "  with message: " + message + StringUtils.NEWLINE + ExceptionUtil.exceptionFollowedByRootCausesToString(exc)
                                            + StringUtils.NEWLINE);

            throw exc;
        }
        catch (Error exc)
        {
            Log.getLogger(LogCategories.MESSAGE_GENERAL).error(
                            "Error when invoking service: " + (service == null ? "(none)" : service.getId()) + StringUtils.NEWLINE + "  with message: " + message + StringUtils.NEWLINE + ExceptionUtil.exceptionFollowedByRootCausesToString(exc)
                                            + StringUtils.NEWLINE);

            throw exc;
        }
    }

    /** @exclude */
    public AsyncMessage routeCommandToService(CommandMessage command, Endpoint endpoint)
    {
        // Make sure command has a messageId
        checkMessageId(command);

        String destId = command.getDestination();

        AsyncMessage replyMessage = null;
        Service service = null;
        String serviceId = null;
        Object commandResult = null;
        boolean serviced = false;

        // Forward login and logout commands to AuthenticationService
        if (command.getOperation() == CommandMessage.LOGIN_OPERATION || command.getOperation() == CommandMessage.LOGOUT_OPERATION)
            serviceId = "authentication-service";
        else
            serviceId = (String) destinationToService.get(destId);

        service = (Service) services.get(serviceId);
        if (service != null)
        {
            // Before passing the message to the service, need to check
            // the security constraints.
            Destination destination = service.getDestination(destId);
            if (destination != null)
                inspectOperation(command, destination);

            try
            {
                extractRemoteCredentials(service, command);
                commandResult = service.serviceCommand(command);
                serviced = true;
            }
            catch (UnsupportedOperationException e)
            {
                ServiceException se = new ServiceException();
                se.setMessage(SERVICE_CMD_NOT_SUPPORTED, new Object[] { service.getClass().getName() });
                throw se;
            }
            catch (SecurityException se)
            {
                // when a LOGIN message causes a security exception, we want to continue processing here
                // to allow metadata to be sent to clients communicating with runtime destinations.
                // The result will be an error message with a login fault message as well as the metadata
                if (serviceId.equals("authentication-service"))
                {
                    commandResult = se.createErrorMessage();
                    if (Log.isDebug())
                        Log.getLogger(LOG_CATEGORY).debug("Security error for message: " + se.toString() + StringUtils.NEWLINE + "  incomingMessage: " + command + StringUtils.NEWLINE + "  errorReply: " + commandResult);
                    serviced = true;
                }
                else
                {
                    throw se;
                }
            }
        }

        if (commandResult == null)
        {
            replyMessage = new AcknowledgeMessage();
        }
        else if (commandResult instanceof AsyncMessage)
        {
            replyMessage = (AsyncMessage) commandResult;
        }
        else
        {
            replyMessage = new AcknowledgeMessage();
            replyMessage.setBody(commandResult);
        }

        // Update the replyMessage body with server configuration if the
        // operation is ping or login and make sure to return the FlexClient Id value.
        if (command.getOperation() == CommandMessage.CLIENT_PING_OPERATION || command.getOperation() == CommandMessage.LOGIN_OPERATION)
        {
            boolean needsConfig = false;
            if (command.getHeader(CommandMessage.NEEDS_CONFIG_HEADER) != null)
                needsConfig = ((Boolean) (command.getHeader(CommandMessage.NEEDS_CONFIG_HEADER))).booleanValue();

            // Send configuration information only if the client requested.
            if (needsConfig)
            {
                ConfigMap serverConfig = describeServices(endpoint);
                if (serverConfig.size() > 0)
                    replyMessage.setBody(serverConfig);
            }

            // Record the features available over this endpoint
            double msgVersion = endpoint.getMessagingVersion();
            if (msgVersion > 0)
                replyMessage.setHeader(CommandMessage.MESSAGING_VERSION, new Double(msgVersion));

            // Record the flex client ID
            FlexClient flexClient = FlexContext.getFlexClient();
            if (flexClient != null)
                replyMessage.setHeader(Message.FLEX_CLIENT_ID_HEADER, flexClient.getId());
        }
        else if (!serviced)
        {
            MessageException lme = new MessageException();
            // No destination with id ''{0}'' is registered with any service.
            lme.setMessage(NO_SERVICE_FOR_DEST, new Object[] { destId });
            throw lme;
        }

        replyMessage.setCorrelationId(command.getMessageId());
        replyMessage.setClientId(command.getClientId());
        if (replyMessage.getBody() instanceof java.util.List)
        {
            replyMessage.setBody(((List) replyMessage.getBody()).toArray());
        }

        if (Log.isDebug())
            Log.getLogger(getLogCategory(command)).debug(
                            "Executed command: " + (service == null ? "(default service)" : "service=" + service.getId()) + StringUtils.NEWLINE + "  commandMessage: " + command + StringUtils.NEWLINE + "  replyMessage: " + replyMessage + StringUtils.NEWLINE);

        return replyMessage;
    }

    /**
     * Services call this method in order to send a message to a FlexClient.
     * 
     * @exclude
     */
    public void routeMessageToMessageClient(Message message, MessageClient messageClient)
    {
        // Make sure message has a messageId
        checkMessageId(message);

        // Route the message and the MessageClient (subscription) to the FlexClient to
        // queue the message for delivery to the remote client.
        // Reset the thread local FlexClient and FlexSession to be specific to the client
        // we're pushing to, and then reset the context back to its original request handling state.
        FlexClient requestFlexClient = FlexContext.getFlexClient();
        FlexSession requestFlexSession = FlexContext.getFlexSession();

        FlexClient pushFlexClient = messageClient.getFlexClient();
        FlexContext.setThreadLocalFlexClient(pushFlexClient);
        FlexContext.setThreadLocalSession(null); // Null because we don't have a currently active endpoint for the push client.

        pushFlexClient.push(message, messageClient);

        // and reset thread locals.
        FlexContext.setThreadLocalFlexClient(requestFlexClient);
        FlexContext.setThreadLocalSession(requestFlexSession);
    }

    /**
     * @exclude Utility method to make sure that message has an assigned messageId.
     */
    private void checkMessageId(Message message)
    {
        if (message.getMessageId() == null)
        {
            MessageException lme = new MessageException();
            lme.setMessage(NULL_MESSAGE_ID);
            throw lme;
        }
    }

    /**
     * Check the security constraint of this operation against an incoming message.
     * 
     * @exclude
     */
    public void inspectOperation(Message message, Destination destination)
    {
        inspectChannel(message, destination);
        loginManager.checkConstraint(destination.getSecurityConstraint());
    }

    /**
     * Verify that this destination permits access over this endpoint.
     * 
     * @exclude
     */
    public void inspectChannel(Message message, Destination destination)
    {
        if (message.getHeader(Message.VALIDATE_ENDPOINT_HEADER) != null)
        {
            String messageChannel = (String) message.getHeader(Message.ENDPOINT_HEADER);
            for (Iterator iter = destination.getChannels().iterator(); iter.hasNext();)
            {
                String channelId = (String) iter.next();
                if (channelId.equals(messageChannel))
                {
                    return;
                }
            }
            MessageException lme = new MessageException();
            lme.setMessage(DESTINATION_UNACCESSIBLE, new Object[] { destination.getId(), messageChannel });
            throw lme;
        }
    }

    /**
     * Check the headers for the message for the RemoteCredentials.
     * 
     * @param service
     * @param message
     * 
     * @exclude
     */
    protected void extractRemoteCredentials(Service service, Message message)
    {
        if (message.headerExists(Message.REMOTE_CREDENTIALS_HEADER))
        {
            boolean setting = false;
            String username = null;
            Object credentials = null;
            if (message.getHeader(Message.REMOTE_CREDENTIALS_HEADER) instanceof String)
            {
                String encoded = (String) message.getHeader(Message.REMOTE_CREDENTIALS_HEADER);
                if (encoded.length() > 0) // empty string is clearing the credentials
                {
                    setting = true;
                    Base64.Decoder decoder = new Base64.Decoder();
                    decoder.decode(encoded);
                    byte[] decodedBytes = decoder.drain();
                    String decoded = "";

                    String charset = (String) message.getHeader(Message.REMOTE_CREDENTIALS_CHARSET_HEADER);
                    if (charset != null)
                    {
                        try
                        {
                            decoded = new String(decodedBytes, charset);
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            MessageException lme = new MessageException();
                            lme.setMessage(UNKNOWN_REMOTE_CREDENTIALS_FORMAT);
                            throw lme;
                        }
                    }
                    else
                    {
                        decoded = new String(decodedBytes);
                    }

                    int colon = decoded.indexOf(":");
                    if (colon > 0 && colon < decoded.length() - 1)
                    {
                        username = decoded.substring(0, colon);
                        credentials = decoded.substring(colon + 1);
                    }
                }
            }
            else
            {
                MessageException lme = new MessageException();
                lme.setMessage(UNKNOWN_REMOTE_CREDENTIALS_FORMAT);
                throw lme;
            }

            if (setting)
            {
                FlexContext.getFlexSession().putRemoteCredentials(new FlexRemoteCredentials(service.getId(), message.getDestination(), username, credentials));
            }
            else
            {
                FlexContext.getFlexSession().clearRemoteCredentials(service.getId(), message.getDestination());
            }
        }
    }

    /**
     * @exclude Returns the logging category to use for a given message.
     */
    public String getLogCategory(Message message)
    {
        if (message instanceof AbstractMessage)
            return ((AbstractMessage) message).logCategory();
        else
            return LogCategories.MESSAGE_GENERAL;
    }

    /**
     * This is the class loader used by the system to load user defined classes.
     * 
     * @return <code>ClassLoader</code> the system should use to load user definied classes.
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * @exclude Used internally by AbstractService to add destination and service id mapping to destinationToService map.
     * 
     * @param destId
     *            Destination id.
     * @param svcId
     *            Service id.
     */
    public void registerDestination(String destId, String svcId)
    {
        // Do not allow multiple destinations with the same id across services
        if (destinationToService.containsKey(destId))
        {
            // Cannot add destination with id ''{0}'' to service with id ''{1}'' because another service with id ''{2}'' already has a destination with the same id.
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage(ConfigurationConstants.DUPLICATE_DEST_ID, new Object[] { destId, svcId, destinationToService.get(destId) });
            throw ex;
        }
        destinationToService.put(destId, svcId);
    }

    /**
     * @exclude Used internally by AbstractService to remove destination and service id mapping from destinationToService map.
     * 
     * @param destId
     *            Destination id.
     */
    public void unregisterDestination(String destId)
    {
        destinationToService.remove(destId);
    }

    private void registerMessageBroker()
    {
        String mbid = getId();

        synchronized (messageBrokers)
        {
            if (messageBrokers.get(mbid) != null)
            {
                ConfigurationException ce = new ConfigurationException();
                ce.setMessage(10137, new Object[] { getId() == null ? "(no value supplied)" : mbid });
                throw ce;
            }
            messageBrokers.put(mbid, this);
        }
    }

    private void unRegisterMessageBroker()
    {
        String mbid = getId();

        synchronized (messageBrokers)
        {
            messageBrokers.remove(mbid);
        }
    }

    @Override
    protected String getLogCategory()
    {
        return LOG_CATEGORY;
    }

    /**
     * Increments the count of destinations actively using an Application or Session level scoped assembler identified by the passed in attributeId.
     * 
     * @param attributeId
     *            Attribute id for the session or application-scoped object.
     */
    public void incrementAttributeIdRefCount(String attributeId)
    {
        synchronized (attributeIdRefCounts)
        {
            Integer currentCount = (Integer) attributeIdRefCounts.get(attributeId);
            if (currentCount == null)
            {
                attributeIdRefCounts.put(attributeId, INTEGER_ONE);
            }
            else
            {
                attributeIdRefCounts.put(attributeId, new Integer(currentCount.intValue() + 1));
            }
        }
    }

    /**
     * Decrements the count of destinations actively using an Application or Session level scoped assembler identified by the passed in attributeId.
     * 
     * @param attributeId
     *            Attribute id for the session or application-scoped object.
     */
    public int decrementAttributeIdRefCount(String attributeId)
    {
        synchronized (attributeIdRefCounts)
        {
            Integer currentCount = (Integer) attributeIdRefCounts.get(attributeId);
            if (currentCount == null)
            {
                return 0;
            }
            else
            {
                int newValue = currentCount.intValue() - 1;
                attributeIdRefCounts.put(attributeId, new Integer(newValue));
                return newValue;
            }
        }
    }
}
