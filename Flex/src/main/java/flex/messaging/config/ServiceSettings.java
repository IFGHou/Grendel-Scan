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
package flex.messaging.config;



// TODO UCdetector: Remove unused code: 
// /**
//  * A service represents a high-level grouping of
//  * functionality to which the message broker can
//  * delegate messages. Services specify which
//  * message types they're interested in and use
//  * adapters to carry out a message's for a
//  * destination.
//  * <p>
//  * A service maintains a list of destinations which
//  * effectively represents a &quot;whitelist&quot;
//  * of actions allowed by that service.
//  * </p>
//  *
//  * @author Peter Farland
//  * @exclude
//  */
// public class ServiceSettings extends PropertiesSettings
// {
//     private final String id;
//     private String sourceFile;
//     private String className;
// 
//     private AdapterSettings defaultAdapterSettings;
//     private final Map adapterSettings;
//     private final List defaultChannels;
//     private final Map destinationSettings;
//     private SecurityConstraint securityConstraint;
// 
//     public ServiceSettings(String id)
//     {
//         this.id = id;
//         destinationSettings = new HashMap();
//         adapterSettings = new HashMap(2);
//         defaultChannels = new ArrayList(4);
//     }
// 
//     public String getId()
//     {
//         return id;
//     }
// 
//     String getSourceFile()
//     {
//         return sourceFile;
//     }
// 
//     void setSourceFile(String sourceFile)
//     {
//         this.sourceFile = sourceFile;
//     }
// 
//     public String getClassName()
//     {
//         return className;
//     }
// 
//     public void setClassName(String name)
//     {
//         className = name;
//     }
// 
//     /*
//      *  SERVER ADAPTERS
//      */
//     public AdapterSettings getDefaultAdapter()
//     {
//         return defaultAdapterSettings;
//     }
// 
//     public AdapterSettings getAdapterSettings(String id)
//     {
//         return (AdapterSettings)adapterSettings.get(id);
//     }
// 
//     public Map getAllAdapterSettings()
//     {
//         return adapterSettings;
//     }
// 
//     public void addAdapterSettings(AdapterSettings a)
//     {
//         adapterSettings.put(a.getId(), a);
//         if (a.isDefault())
//         {
//             defaultAdapterSettings = a;
//         }
//     }
// 
//     /*
//      *  DEFAULT CHANNELS
//      */
//     public void addDefaultChannel(ChannelSettings c)
//     {
//         defaultChannels.add(c);
//     }
// 
//     public List getDefaultChannels()
//     {
//         return defaultChannels;
//     }
// 
//     /*
//      *  DEFAULT SECURITY
//      */
// 
//     /**
//      * Gets the <code>SecurityConstraint</code> that will be applied to all
//      * destinations of the service, or <code>null</code> if no constraint has
//      * been registered.
//      *
//      * @return the <code>SecurityConstraint</code> for this service.
//      */
//     public SecurityConstraint getConstraint()
//     {
//         return securityConstraint;
//     }
// 
//     /**
//      * Sets the security constraint to be applied to all destinations of the service.
//      * Security constraints restrict which clients can contact this destination. Use
//      * <code>null</code> to remove an existing constraint.
//      *
//      * @param sc the <code>SecurityConstraint</code> to apply to this
//      * service.
//      */
//     public void setConstraint(SecurityConstraint sc)
//     {
//         securityConstraint = sc;
//     }
// 
//     /*
//      *  DESTINATIONS
//      */
//     public Map getDestinationSettings()
//     {
//         return destinationSettings;
//     }
// 
//     public void addDestinationSettings(DestinationSettings dest)
//     {
//         destinationSettings.put(dest.getId(), dest);
//     }
// }
