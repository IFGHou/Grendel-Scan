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
package flex.messaging.config;

/**
 * A service must register the adapters that it will use to process messages. Each destination selects an adapter that processes the request by referring to it by id.
 * <p>
 * Adapters can also be configured with initialization properties.
 * </p>
 * 
 * @see flex.messaging.services.ServiceAdapter
 * @author Peter Farland
 * @exclude
 */
public class AdapterSettings extends PropertiesSettings
{
    // private final String id;
    private String sourceFile;
    private String className;
    private boolean defaultAdapter;

    // TODO UCdetector: Remove unused code:
    // /**
    // * Used to construct a new set of properties to describe an adapter. Note
    // * that an identity is required in order for destinations to refer to this
    // * adapter.
    // *
    // * @param id the <code>String</code> representing the unique identity for
    // * this adapter.
    // */
    // public AdapterSettings(String id)
    // {
    // super();
    // this.id = id;
    // }

    /**
     * The identity that destinations will refer to when assigning and adapter.
     * 
     * @return the adapter identity as a <code>String</code>.
     */
    // public String getId()
    // {
    // return id;
    // }

    /**
     * Gets the name of the Java class implementation for this adapter.
     * 
     * @return String The name of the adapter implementation.
     * @see flex.messaging.services.ServiceAdapter
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Sets name of the Java class implementation for this adapter. The implementation is resolved from the current classpath and must extend <code>flex.messaging.services.ServiceAdapter</code>.
     * 
     * @param name
     *            the <code>String</code>
     */
    public void setClassName(String name)
    {
        className = name;
    }

    /**
     * Returns a boolean flag that determines whether this adapter is the default for a service's destinations. Only one default adapter can be set for a given service.
     * 
     * @return boolean true if this adapter is the default.
     */
    public boolean isDefault()
    {
        return defaultAdapter;
    }

    /**
     * Sets a flag to determine whether an adapter will be used as the default (for example, in the event that a destination did not specify an adapter explicitly).
     * 
     * Only one default can be set for a given service.
     * 
     * @param b
     *            a <code>boolean</code> flag, true if this adapter should be used as the default for the service.
     */
    public void setDefault(boolean b)
    {
        defaultAdapter = b;
    }

    /**
     * Internal use only.
     * 
     * @exclude
     */
    String getSourceFile()
    {
        return sourceFile;
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Internal use only.
    // * @exclude
    // */
    // void setSourceFile(String file)
    // {
    // this.sourceFile = file;
    // }
}
