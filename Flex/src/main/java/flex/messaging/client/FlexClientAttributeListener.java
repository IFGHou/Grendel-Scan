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
package flex.messaging.client;

/**
 * Interface for FlexClient attribute listeners.
 */
public interface FlexClientAttributeListener 
{
    /**
     * Callback invoked after an attribute is added to the FlexClient.
     * 
     * @param event The event containing the associated FlexClient and attribute
     *              information.
     */
    void attributeAdded(FlexClientBindingEvent event);
    
    /**
     * Callback invoked after an attribute is removed from the FlexClient.
     * 
     * @param event The event containing the associated FlexClient and attribute
     *              information.
     */
    void attributeReplaced(FlexClientBindingEvent event);
    
    /**
     * Callback invoked after an attribute has been replaced with a new value.
     * 
     * @param event The event containing the associated FlexClient and attribute
     *              information.
     */
    void attributeRemoved(FlexClientBindingEvent event);
}
