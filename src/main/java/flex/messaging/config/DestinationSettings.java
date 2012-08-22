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

import java.util.List;

/**
 * Destinations are configured for a particular <code>Service</code>. A
 * destination's configuration includes an <code>id</code> attribute to provide
 * a public name for clients to use when sending messages.
 * <p>
 * The configuration also specifies which channels are valid to contact the
 * destination, as well as which adapter a service must use to process
 * client messages for this destination and any security constraints that need
 * to be enforced.
 * </p>
 *
 * @see flex.messaging.Destination
 * @author Peter Farland
 * @exclude
 */
public class DestinationSettings extends PropertiesSettings
{
    /**
     * @exclude
     */
    public static final String SERVER_ELEMENT = "server";

//    private final String id;
    private String sourceFile;
    private List channelSettings;
    private AdapterSettings adapterSettings;
    private SecurityConstraint constraint;




// TODO UCdetector: Remove unused code: 
//       /**
//        * Used to construct a new set of properties to describe a destination.
//        * Note that an identity is required in order for clients to refer to a
//        * destination.
//        *
//        * @param id A string representing the unique identity of this destination.
//        */
//       public DestinationSettings(String id)
//       {
//           this.id = id;
//           channelSettings = new ArrayList();
//       }

    /**
     * Gets the unique identity used by clients to target a destination.
     *
     * @return String the destination's id.
     */
//    public String getId()
//    {
//        return id;
//    }

    /**
     * Internal use only.
     * @exclude
     */
    String getSourceFile()
    {
        return sourceFile;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * Internal use only.
//      * @exclude
//      */
//     void setSourceFile(String sourceFile)
//     {
//         this.sourceFile = sourceFile;
//     }

    /*
     *  CHANNEL SETTINGS
     */


// TODO UCdetector: Remove unused code: 
//     /**
//      * Adds a channel to the set of channels that should be used to contact
//      * this destination. The order in which channels are added is significant
//      * (clients use this order to locate an available channel and failover to
//      * the next in the list on failure).
//      *
//      * @param c the <code>ChannelSettings</code> to add to the set of
//      * channel definitions for this destination.
//      */
//     public void addChannelSettings(ChannelSettings c)
//     {
//         if (c != null)
//         {
//             channelSettings.add(c);
//         }
//     }

    /**
     * Overrides the set of channels that can be used to contact this
     * destination.
     *
     * @param settings A List of <code>ChannelSettings</code>.
     */
    public void setChannelSettings(List settings)
    {
        channelSettings = settings;
    }

    /**
     * Gets the set of channels that can be used to contact this destination.
     *
     * @return a <code>java.util.List</code> of <code>ChannelSetting</code>s
     * describing the channels that can be used to contact this destination.
     */
    public List getChannelSettings()
    {
        return channelSettings;
    }


    /*
     *  SECURITY
     */

    /**
     * Gets the <code>SecurityConstraint</code> that will be applied to this
     * destination, or <code>null</code> if no constraint has been registered.
     *
     * @return the <code>SecurityConstraint</code> for this destination.
     */
    public SecurityConstraint getConstraint()
    {
        return constraint;
    }

    /**
     * Sets the security constraint to be applied to this destination. Security
     * constraints restrict which clients can contact this destination. Use
     * <code>null</code> to remove an existing constraint.
     *
     * @param sc the <code>SecurityConstraint</code> to apply to this
     * destination.
     */
    public void setConstraint(SecurityConstraint sc)
    {
        constraint = sc;
    }

    /*
     *  SERVICE ADAPTER
     */

    /**
     * Sets the service adapter to be used when the managing service is
     * processing messages bound for this destination.
     *
     * @param a The <code>AdapterSettings</code> that describe the adapter
     * to use for this destination.
     */
    public void setAdapterSettings(AdapterSettings a)
    {
        adapterSettings = a;
    }

    /**
     * Gets the adapter to be used for this destination.
     *
     * @return <code>AdapterSettings</code> for this destinations adapter.
     * A <code>null</code> value implies the the default service adapter should
     * be used.
     */
    public AdapterSettings getAdapterSettings()
    {
        return adapterSettings;
    }


}
