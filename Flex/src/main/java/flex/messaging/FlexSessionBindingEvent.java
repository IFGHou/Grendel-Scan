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
package flex.messaging;

/**
 * Event used to notify FlexSessionAttributeListeners of changes to session
 * attributes.
 */
public class FlexSessionBindingEvent
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructs an event for an attribute that is bound or unbound from a session.
     * 
     * @param session The associated session.
     * @param name The attribute name.
     */
    public FlexSessionBindingEvent(FlexSession session, String name)
    {
        this.session = session;
        this.name = name;
    }
    
    /**
     * Constructs an event for an attribute that is added to a session or 
     * replaced by a new value.
     * 
     * @param session The associated session.
     * @param name The attribute name.
     * @param value The attribute value.
     */
    public FlexSessionBindingEvent(FlexSession session, String name, Object value)
    {
        this.session = session;
        this.name = name;
        this.value = value;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * The session that generated the event.
     */
    private FlexSession session;
    
    /**
     * The name of the attribute associated with the event.
     */
    private String name;
    
    /**
     * The value of the attribute associated with the event.
     */
    private Object value;
    
    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     * Returns the Flex session that generated the event.
     * 
     * @return The Flex session that generated the event.
     */
    public FlexSession getSession()
    {
        return session;
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
