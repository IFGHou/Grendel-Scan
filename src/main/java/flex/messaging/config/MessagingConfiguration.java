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
package flex.messaging.config;



// TODO UCdetector: Remove unused code: 
// /**
//  * This object encapsulates settings for a MessageBroker instance.
//  * The MessageBroker itself has no knowledge of configuration specifics;
//  * instead, this object sets the relevant values on the broker using
//  * information which a ConfigurationParser has provided for it.
//  *
//  * @author Peter Farland
//  * @author neville
//  * @exclude
//  */
// public class MessagingConfiguration implements ServicesConfiguration
// {
//     private final Map channelSettings;
//     private final List defaultChannels;
//     private final SecuritySettings securitySettings;
//     private final List serviceSettings;
//     private final List sharedServerSettings;
//     private LoggingSettings loggingSettings;
//     private SystemSettings systemSettings;
//     private FlexClientSettings flexClientSettings;
//     private final Map clusterSettings;
//     private final Map factorySettings;
// 
//     public MessagingConfiguration()
//     {
//         channelSettings = new HashMap();
//         defaultChannels = new ArrayList(4);
//         clusterSettings = new HashMap();
//         factorySettings = new HashMap();
//         serviceSettings = new ArrayList();
//         sharedServerSettings = new ArrayList();
//         securitySettings = new SecuritySettings();
//     }
// 
// /* TODO UCdetector: Remove unused code: 
//     public void configureBroker(MessageBroker broker)
//     {
//         broker.setChannelSettings(channelSettings);
//         broker.setSecuritySettings(securitySettings);
//         broker.setSystemSettings(systemSettings);
//         broker.setFlexClientSettings(flexClientSettings);
//         createAuthorizationManager(broker);
//         createFlexClientManager(broker);
//         createRedeployManager(broker);
//         createFactories(broker);
//         createSharedServers(broker);
//         createEndpoints(broker);
//         // Default channels have to be set after endpoints are created.
//         broker.setDefaultChannels(defaultChannels);
//         prepareClusters(broker);
//         createServices(broker);
//     }
// */
// 
//     public MessageBroker createBroker(String id, ClassLoader loader)
//     {
//         return new MessageBroker(systemSettings.isManageable(), id, loader);
//     }
// 
//     private void createFactories(MessageBroker broker)
//     {
//         for (Iterator iter=factorySettings.entrySet().iterator(); iter.hasNext(); )
//         {
//             Map.Entry entry = (Map.Entry) iter.next();
//             String id = (String) entry.getKey();
//             FactorySettings factorySetting = (FactorySettings) entry.getValue();
//             broker.addFactory(id, factorySetting.createFactory());
//         }
//     }
// 
//     private void createFlexClientManager(MessageBroker broker)
//     {
//         FlexClientManager flexClientManager = new FlexClientManager(broker.isManaged(), broker);
//         broker.setFlexClientManager(flexClientManager);
//     }
// 
//     private void createRedeployManager(MessageBroker broker)
//     {
//         RedeployManager redeployManager = new RedeployManager();
//         redeployManager.setEnabled(systemSettings.getRedeployEnabled());
//         redeployManager.setWatchInterval(systemSettings.getWatchInterval());
//         redeployManager.setTouchFiles(systemSettings.getTouchFiles());
//         redeployManager.setWatchFiles(systemSettings.getWatchFiles());
//         broker.setRedeployManager(redeployManager);
//     }
// 
//     private void createAuthorizationManager(MessageBroker broker)
//     {
//         LoginManager loginManager = new LoginManager();
// 
//         // Create a Login Command for the LoginManager.
//         LoginCommand loginCommand = null;
// 
//         Map loginCommands = securitySettings.getLoginCommands();
// 
//         // If default Login Command is enabled, use it.
//         LoginCommandSettings loginCommandSettings = (LoginCommandSettings)loginCommands.get(LoginCommandSettings.SERVER_MATCH_OVERRIDE);
//         if (loginCommandSettings != null)
//         {
//             loginCommand = initLoginCommand(loginCommandSettings);
//         }
//         // Otherwise, try a server specific Login Command.
//         else
//         {
//             String serverInfo = securitySettings.getServerInfo();
//             loginCommandSettings = (LoginCommandSettings)loginCommands.get(serverInfo);
// 
//             if (loginCommandSettings != null)
//             {
//                 loginCommand = initLoginCommand(loginCommandSettings);
//             }
//             else
//             {
//                 // Try a partial match of serverInfo
//                 serverInfo = serverInfo.toLowerCase();
//                 for (Iterator iterator = loginCommands.keySet().iterator(); iterator.hasNext();)
//                 {
//                     String serverMatch = (String)iterator.next();
//                     loginCommandSettings = (LoginCommandSettings)loginCommands.get(serverMatch);
// 
//                     if (serverInfo.indexOf(serverMatch.toLowerCase()) != -1)
//                     {
//                         // add this match for easier lookup next time around
//                         loginCommands.put(serverInfo, loginCommandSettings);
//                         loginCommand = initLoginCommand(loginCommandSettings);
//                         break;
//                     }
//                 }
//             }
//         }
// 
//         if (loginCommand == null)
//         {
//             if (Log.isWarn())
//                 Log.getLogger(ConfigurationManager.LOG_CATEGORY).warn
//                 ("No login command was found for '" + securitySettings.getServerInfo() 
//                         + "'. Please ensure that the login-command tag has the correct server attribute value" 
//                         + ", or use 'all' to use the login command regardless of the server.");
//         }
//         else
//         {
//             loginManager.setLoginCommand(loginCommand);
//         }
// 
//         if (loginCommandSettings != null)
//             loginManager.setPerClientAuthentication(loginCommandSettings.isPerClientAuthentication());
// 
//         broker.setLoginManager(loginManager);
//     }
// 
//     private LoginCommand initLoginCommand(LoginCommandSettings loginCommandSettings)
//     {
//         String loginClass = loginCommandSettings.getClassName();
//         Class c = ClassUtil.createClass(loginClass,
//                 FlexContext.getMessageBroker() == null ? null :
//                 FlexContext.getMessageBroker().getClassLoader());
//         LoginCommand loginCommand = (LoginCommand)ClassUtil.createDefaultInstance(c, LoginCommand.class);
// 
//         return loginCommand;
//     }
// 
//     private void createSharedServers(MessageBroker broker)
//     {
//         int n = sharedServerSettings.size();
//         for (int i = 0; i < n; i++)
//         {
//             SharedServerSettings settings = (SharedServerSettings)sharedServerSettings.get(i);
//             String id = settings.getId();
//             String className = settings.getClassName();
//             Class serverClass = ClassUtil.createClass(className, broker.getClassLoader());
//             Server server = (Server)ClassUtil.createDefaultInstance(serverClass, Server.class);
//             server.initialize(id, settings.getProperties());
//             if (broker.isManaged() && (server instanceof ManageableComponent))
//             {
//                 ManageableComponent manageableServer = (ManageableComponent)server;
//                 manageableServer.setManaged(true);
//                 manageableServer.setParent(broker);
//             }
//             broker.addServer(server);
// 
//             if (Log.isInfo())
//             {
//                 Log.getLogger(ConfigurationManager.LOG_CATEGORY).info
//                 ("Server " + id + " of type " + className + " created.");
//             }
//         }
//     }
// 
//     private void createEndpoints(MessageBroker broker)
//     {
//         for (Iterator iter = channelSettings.keySet().iterator(); iter.hasNext();)
//         {
//             String id = (String)iter.next();
//             ChannelSettings chanSettings = (ChannelSettings)channelSettings.get(id);
//             String url = chanSettings.getUri();
//             String endpointClassName = chanSettings.getEndpointType();
// 
//             // Skip channel-definitions for remote endpoints
//             if (chanSettings.isRemote())
//                 continue;
// 
//             // Create the Endpoint
//             Endpoint endpoint = broker.createEndpoint(id, url, endpointClassName);
//             endpoint.setSecurityConstraint(chanSettings.getConstraint());
//             endpoint.setClientType(chanSettings.getClientType());
// 
//             // Assign referenced server
//             String referencedServerId = chanSettings.getServerId();
//             if ((referencedServerId != null) && (endpoint instanceof Endpoint2))
//             {
//                 Server server = broker.getServer(referencedServerId);
//                 if (server == null)
//                 {
//                     ConfigurationException ce = new ConfigurationException();
//                     ce.setMessage(11128, new Object[] {chanSettings.getId(), referencedServerId});
//                     throw ce;
//                 }
//                 ((Endpoint2)endpoint).setServer(broker.getServer(referencedServerId));
//             }
// 
//             // Initialize with endpoint properties
//             endpoint.initialize(id, chanSettings.getProperties());
// 
//             if (Log.isInfo())
//             {
//                 String endpointURL = endpoint.getUrl();
//                 String endpointSecurity = EndpointControl.getSecurityConstraintOf(endpoint);
//                 if (StringUtils.isEmpty(endpointSecurity))
//                     endpointSecurity = "None";
//                 Log.getLogger(ConfigurationManager.LOG_CATEGORY).info
//                 ("Endpoint " + id + " created with security: " +
//                         endpointSecurity + StringUtils.NEWLINE +
//                         "at URL: " + endpointURL);
//             }
//         }
//     }
// 
//     private void createServices(MessageBroker broker)
//     {
//         //the broker needs its AuthenticationService always
//         AuthenticationService authService = new AuthenticationService();
//         authService.setMessageBroker(broker);
// 
//         for (Iterator iter = serviceSettings.iterator(); iter.hasNext();)
//         {
//             ServiceSettings svcSettings = (ServiceSettings)iter.next();
//             String svcId = svcSettings.getId();
//             String svcClassName = svcSettings.getClassName();
// 
//             // Create the Service
//             Service service = broker.createService(svcId, svcClassName);
// 
//             // Service Class Name - not needed in AbstractService
// 
//             // Initialize with service properties
//             service.initialize(svcId, svcSettings.getProperties());
// 
//             // Default Channels
//             for (Iterator chanIter = svcSettings.getDefaultChannels().iterator(); chanIter.hasNext();)
//             {
//                 ChannelSettings chanSettings = (ChannelSettings)chanIter.next();
//                 service.addDefaultChannel(chanSettings.getId());
//             }
// 
//             // Adapter Definitions
//             Map svcAdapterSettings = svcSettings.getAllAdapterSettings();
//             for (Iterator asIter = svcAdapterSettings.values().iterator(); asIter.hasNext();)
//             {
//                 AdapterSettings as = (AdapterSettings) asIter.next();
//                 service.registerAdapter(as.getId(), as.getClassName());
//                 if (as.isDefault())
//                 {
//                     service.setDefaultAdapter(as.getId());
//                 }
//             }
// 
//             // Destinations
//             Map destinationSettings = svcSettings.getDestinationSettings();
//             for (Iterator destSettingsIter = destinationSettings.keySet().iterator(); destSettingsIter.hasNext();)
//             {
//                 String destName = (String)destSettingsIter.next();
//                 DestinationSettings destSettings = (DestinationSettings)destinationSettings.get(destName);
// 
//                 createDestination(destSettings, service, svcSettings);
//             }
//         }
//     }
// 
//     private void createDestination(DestinationSettings destSettings, Service service, ServiceSettings svcSettings)
//     {
//         String destId = destSettings.getId();
//         Destination destination = service.createDestination(destId);
// 
//         // Channels
//         List chanSettings = destSettings.getChannelSettings();
//         if (chanSettings.size() > 0)
//         {
//             List channelIds = new ArrayList(2);
//             for (Iterator iter = chanSettings.iterator(); iter.hasNext();) {
//                 ChannelSettings cs = (ChannelSettings) iter.next();
//                 channelIds.add(cs.getId());
//             }
//             destination.setChannels(channelIds);
//         }
// 
//         // Security
//         SecurityConstraint constraint = destSettings.getConstraint();
//         destination.setSecurityConstraint(constraint);
// 
//         // Initialize with service, adapter and destination properties
//         destination.initialize(destId, svcSettings.getProperties());
//         destination.initialize(destId, destSettings.getAdapterSettings().getProperties());
//         destination.initialize(destId, destSettings.getProperties());
// 
//         // Service Adapter
//         createAdapter(destination, destSettings, svcSettings);
//     }
// 
//     private void createAdapter(Destination destination, DestinationSettings destSettings, ServiceSettings svcSettings)
//     {
//         AdapterSettings adapterSettings = destSettings.getAdapterSettings();
//         String adapterId = adapterSettings.getId();
// 
//         ServiceAdapter adapter = destination.createAdapter(adapterId);
// 
//         // Initialize with service, adapter and then destination properties
//         adapter.initialize(adapterId, svcSettings.getProperties());
//         adapter.initialize(adapterId, adapterSettings.getProperties());
//         adapter.initialize(adapterId, destSettings.getProperties());
// 
//     }
// 
// 
// // TODO UCdetector: Remove unused code: 
// //     /**
// //      * @exclude
// //      * Used by the MessageBrokerServlet to set up the singleton Log instance
// //      * and add any targets defined in the logging configuration.
// //      * This needs to be invoked ahead of creating and bootstrapping a MessageBroker
// //      * instance so we're sure to have the logging system running in case the bootstrap
// //      * process needs to log anything out.
// //      */
// //     public void createLogAndTargets()
// //     {
// //         if (loggingSettings == null)
// //         {
// //             Log.setPrettyPrinterClass(ToStringPrettyPrinter.class.getName());
// //             return;
// //         }
// // 
// //         Log.createLog();
// // 
// //         ConfigMap properties = loggingSettings.getProperties();
// // 
// //         // Override default pretty printer for FDS to traverse deep Object graphs
// //         if (properties.getPropertyAsString("pretty-printer", null) == null)
// //         {
// //             Log.setPrettyPrinterClass(ToStringPrettyPrinter.class.getName());
// //         }
// // 
// //         Log.initialize(null, properties);
// // 
// //         // Targets
// //         List targets = loggingSettings.getTargets();
// //         Iterator it = targets.iterator();
// //         while (it.hasNext())
// //         {
// //             TargetSettings targetSettings = (TargetSettings)it.next();
// //             String className = targetSettings.getClassName();
// // 
// //             Class c = ClassUtil.createClass(className,
// //                         FlexContext.getMessageBroker() == null ? null :
// //                         FlexContext.getMessageBroker().getClassLoader());
// //             try
// //             {
// //                 Target target = (Target)c.newInstance();
// //                 target.setLevel(Log.readLevel(targetSettings.getLevel()));
// //                 target.setFilters(targetSettings.getFilters());
// //                 target.initialize(null, targetSettings.getProperties());
// //                 Log.addTarget(target);
// //             }
// //             catch (Throwable t)
// //             {
// //                 // Unwrap to get to the interesting exception
// //                 if (t instanceof InvocationTargetException)
// //                     t = ((InvocationTargetException ) t).getCause();
// // 
// //                 System.err.println("*** Error setting up logging system");
// //                 t.printStackTrace();
// // 
// //                 ConfigurationException cx = new ConfigurationException();
// //                 cx.setMessage(10126, new Object[] { className });
// //                 cx.setRootCause(t);
// //                 throw cx;
// //             }
// //         }
// //     }
// 
//     private void prepareClusters(MessageBroker broker)
//     {
//         ClusterManager clusterManager = broker.getClusterManager();
//         for (Iterator iter=clusterSettings.keySet().iterator(); iter.hasNext(); )
//         {
//             String clusterId = (String) iter.next();
//             ClusterSettings cs = (ClusterSettings) clusterSettings.get(clusterId);
//             clusterManager.prepareCluster(cs);
//         }
//     }
// 
//     /*
//      * SHARED SERVER CONFIGURATION
//      */
// /* TODO UCdetector: Remove unused code: 
//     public void addSharedServerSettings(SharedServerSettings settings)
//     {
//         sharedServerSettings.add(settings);
//     }
// */
// 
//     /*
//      * CHANNEL CONFIGURATION
//      */
// 
//     public void addChannelSettings(String id, ChannelSettings settings)
//     {
//         channelSettings.put(id, settings);
//     }
// 
//     public ChannelSettings getChannelSettings(String ref)
//     {
//         return (ChannelSettings) channelSettings.get(ref);
//     }
// 
//     public Map getAllChannelSettings()
//     {
//         return channelSettings;
//     }
// 
//     /*
//      * DEFAULT CHANNELS CONFIGURATION
//      */
//     public void addDefaultChannel(String id)
//     {
//         defaultChannels.add(id);
//     }
// 
//     public List getDefaultChannels()
//     {
//         return defaultChannels;
//     }
// 
//     /*
//      * SECURITY CONFIGURATION
//      */
// 
//     public SecuritySettings getSecuritySettings()
//     {
//         return securitySettings;
//     }
// 
//     /*
//      * SERVICE CONFIGURATION
//      */
// 
//     public void addServiceSettings(ServiceSettings settings)
//     {
//         serviceSettings.add(settings);
//     }
// 
//     public ServiceSettings getServiceSettings(String id)
//     {
//         for (Iterator iter = serviceSettings.iterator(); iter.hasNext();)
//         {
//             ServiceSettings serviceSettings = (ServiceSettings) iter.next();
//             if (serviceSettings.getId().equals(id))
//                 return serviceSettings;
//         }
//         return null;
//     }
// 
//     public List getAllServiceSettings()
//     {
//         return serviceSettings;
//     }
// 
//     /*
//      * LOGGING
//      */
//     public LoggingSettings getLoggingSettings()
//     {
//         return loggingSettings;
//     }
// 
//     public void setLoggingSettings(LoggingSettings loggingSettings)
//     {
//         this.loggingSettings = loggingSettings;
//     }
// 
//     /*
//      * SYSTEM SETTINGS
//      */
//     public void setSystemSettings(SystemSettings ss)
//     {
//         systemSettings = ss;
//     }
// 
//     public SystemSettings getSystemSettings()
//     {
//         return systemSettings;
//     }
// 
//     /*
//      * FLEXCLIENT SETTINGS
//      */
//     public void setFlexClientSettings(FlexClientSettings value)
//     {
//         flexClientSettings = value;
//     }
// 
//     public FlexClientSettings getFlexClientSettings()
//     {
//         return flexClientSettings;
//     }
// 
//     /*
//      * CLUSTER CONFIGURATION
//      */
// 
// /* TODO UCdetector: Remove unused code: 
//     public void addClusterSettings(ClusterSettings settings)
//     {
//         if (settings.isDefault())
//         {
//             for (Iterator it = clusterSettings.values().iterator(); it.hasNext(); )
//             {
//                 ClusterSettings cs = (ClusterSettings) it.next();
// 
//                 if (cs.isDefault())
//                 {
//                     ConfigurationException cx = new ConfigurationException();
//                     cx.setMessage(10214, new Object[] { settings.getClusterName(), cs.getClusterName() });
//                     throw cx;
//                 }
//             }
//         }
//         if (clusterSettings.containsKey(settings.getClusterName()))
//         {
//             ConfigurationException cx = new ConfigurationException();
//             cx.setMessage(10206, new Object[] { settings.getClusterName() });
//             throw cx;
//         }
//         clusterSettings.put(settings.getClusterName(), settings);
//     }
// */
// 
//     public ClusterSettings getClusterSettings(String clusterId)
//     {
//         for (Iterator it = clusterSettings.values().iterator(); it.hasNext(); )
//         {
//             ClusterSettings cs = (ClusterSettings) it.next();
//             if (cs.getClusterName() == clusterId)
//                 return cs; // handle null case
//             if (cs.getClusterName() != null && cs.getClusterName().equals(clusterId))
//                 return cs;
//         }
//         return null;
//     }
// 
//     public ClusterSettings getDefaultCluster()
//     {
//         for (Iterator it = clusterSettings.values().iterator(); it.hasNext(); )
//         {
//             ClusterSettings cs = (ClusterSettings) it.next();
//             if (cs.isDefault())
//                 return cs;
//         }
//         return null;
//     }
// 
//     public void addFactorySettings(String id, FactorySettings settings)
//     {
//         factorySettings.put(id, settings);
//     }
// 
//     /*
//      * VALIDATIONS
//      */
// 
//     public void reportUnusedProperties()
//     {
//         ArrayList findings = new ArrayList();
// 
//         Iterator serviceItr = serviceSettings.iterator();
//         while (serviceItr.hasNext())
//         {
//             ServiceSettings serviceSettings = (ServiceSettings) serviceItr.next();
//             gatherUnusedProperties(serviceSettings.getId(), serviceSettings.getSourceFile(),
//                     ConfigurationConstants.SERVICE_ELEMENT, serviceSettings, findings);
//             Iterator destinationItr = serviceSettings.getDestinationSettings().values().iterator();
//             while (destinationItr.hasNext())
//             {
//                 DestinationSettings destinationSettings = (DestinationSettings) destinationItr.next();
//                 gatherUnusedProperties(destinationSettings.getId(), destinationSettings.getSourceFile(),
//                      ConfigurationConstants.DESTINATION_ELEMENT,
//                      destinationSettings, findings);
// 
//                 AdapterSettings adapterSettings = destinationSettings.getAdapterSettings();
//                 if (adapterSettings != null)
//                 {
//                     gatherUnusedProperties(adapterSettings.getId(), adapterSettings.getSourceFile(),
//                          ConfigurationConstants.ADAPTER_ELEMENT,
//                          adapterSettings, findings);
//                 }
//             }
//         }
// 
//         Iterator channelItr = channelSettings.values().iterator();
//         while (channelItr.hasNext())
//         {
//             ChannelSettings channelSettings = (ChannelSettings) channelItr.next();
//             // Skip property validation for remote channel-definitions
//             if (channelSettings.isRemote())
//                 continue;
// 
//             gatherUnusedProperties(channelSettings.getId(), channelSettings.getSourceFile(),
//             ConfigurationConstants.CHANNEL_ELEMENT, channelSettings, findings);
//         }
//         
//         Iterator serverItr = sharedServerSettings.iterator();
//         while (serverItr.hasNext())
//         {
//             SharedServerSettings serverSettings = (SharedServerSettings)serverItr.next();
//             gatherUnusedProperties(serverSettings.getId(), serverSettings.getSourceFile(), 
//                     ConfigurationConstants.SERVER_ELEMENT, serverSettings, findings);
//         }
// 
//         if (!findings.isEmpty())
//         {
//             int errorNumber = 10149;
//             ConfigurationException exception = new ConfigurationException();
//             StringBuffer allDetails = new StringBuffer();
//             for (int i = 0; i < findings.size(); i++)
//             {
//                 allDetails.append(StringUtils.NEWLINE);
//                 allDetails.append("  ");
//                 exception.setDetails(errorNumber, "pattern", (Object[]) findings.get(i));
//                 allDetails.append(exception.getDetails());
//                 exception.setDetails(null);
//             }
//             exception.setMessage(errorNumber, new Object[] {allDetails});
//             throw exception;
//         }
//     }
// 
//     private void gatherUnusedProperties
//         (String settingsId, String settingsSource, String settingsType,
//          PropertiesSettings settings, Collection result)
//     {
//         List unusedProperties = settings.getProperties().findAllUnusedProperties();
//         int size = unusedProperties.size();
//         if (size > 0)
//         {
//             for (int i = 0; i < size; i++)
//             {
//                 String path = (String) unusedProperties.get(i);
//                 result.add(new Object[] {path, settingsType, settingsId, settingsSource});
//             }
//         }
//     }
// }
