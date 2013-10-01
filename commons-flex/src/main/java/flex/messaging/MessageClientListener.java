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
 * Interface to be notified when a MessageClient is created or destroyed. Implementations of this interface may add themselves as listeners statically via
 * <code>MessageClient.addMessageClientCreatedListener()</code>. To listen for MessageClient destruction, the implementation class instance must add itself as a listener to a specific MessageClient
 * instance via the <code>addMessageClientDestroyedListener()</code> method.
 */
public interface MessageClientListener
{
    /**
     * Notification that a MessageClient was created.
     * 
     * @param messageClient
     *            The MessageClient that was created.
     */
    void messageClientCreated(MessageClient messageClient);

    /**
     * Notification that a MessageClient is about to be destroyed.
     * 
     * @param messageClient
     *            The MessageClient that will be destroyed.
     */
    void messageClientDestroyed(MessageClient messageClient);
}
