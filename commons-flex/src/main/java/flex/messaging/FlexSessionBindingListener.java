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

/**
 * Interface to be notified when the object is bound or unbound from the Flex session.
 */
public interface FlexSessionBindingListener
{
    /**
     * Callback invoked when the object is bound to a Flex session.
     * 
     * @param event
     *            The event containing the associated session and attribute information.
     */
    void valueBound(FlexSessionBindingEvent event);

    /**
     * Callback invoked when the object is unbound from a Flex session.
     * 
     * @param event
     *            The event containing the associated session and attribute information.
     */
    void valueUnbound(FlexSessionBindingEvent event);
}
