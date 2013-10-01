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
 * Interface to be notified when a FlexClient is created or destroyed. Implementations of this interface may add themselves as created listeners statically via
 * <code>FlexClient.addClientCreatedListener()</code>. To listen for FlexClient destruction, the implementation instance must add itself as a listener to a specific FlexClient instance via the
 * <code>addClientDestroyedListener()</code> method.
 */
public interface FlexClientListener
{
    /**
     * Notification that a FlexClient was created.
     * 
     * @param client
     *            The FlexClient that was created.
     */
    void clientCreated(FlexClient client);

    /**
     * Notification that a FlexClient is about to be destroyed.
     * 
     * @param client
     *            The FlexClient that will be destroyed.
     */
    void clientDestroyed(FlexClient client);
}
