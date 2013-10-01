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

/**
 * Event used to notify FlexClientAttributeListeners of changes to FlexClient attributes.
 */
public class FlexClientBindingEvent
{
    // --------------------------------------------------------------------------
    //
    // Constructor
    //
    // --------------------------------------------------------------------------

    /**
     * Constructs an event for an attribute that is bound or unbound from a FlexClient.
     * 
     * @param client
     *            The FlexClient.
     * @param name
     *            The attribute name.
     */
    public FlexClientBindingEvent(FlexClient client, String name)
    {
        this.client = client;
        this.name = name;
    }

    /**
     * Constructs an event for an attribute that is added to a FlexClient or replaced by a new value.
     * 
     * @param client
     *            The FlexClient.
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     */
    public FlexClientBindingEvent(FlexClient client, String name, Object value)
    {
        this.client = client;
        this.name = name;
        this.value = value;
    }

    // --------------------------------------------------------------------------
    //
    // Variables
    //
    // --------------------------------------------------------------------------

    /**
     * The FlexClient that generated the event.
     */
    private FlexClient client;

    /**
     * The name of the attribute associated with the event.
     */
    private String name;

    /**
     * The value of the attribute associated with the event.
     */
    private Object value;

    // --------------------------------------------------------------------------
    //
    // Methods
    //
    // --------------------------------------------------------------------------

    /**
     * Returns the FlexClient that generated the event.
     * 
     * @return The FlexClient that generated the event.
     */
    public FlexClient getClient()
    {
        return client;
    }

    /**
     * Returns the name of the attribute associated with the event.
     * 
     * @return The name of the attribute associated with the event.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the value of the attribute associated with the event.
     * 
     * @return The value of the attribute associated with the event.
     */
    public Object getValue()
    {
        return value;
    }
}
