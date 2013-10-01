/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2008] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging;

import java.util.EventListener;

/**
 * @exclude Provides notification for multicast message routing events to support optimized asynchronous IO to the target remote hosts.
 */
public interface MessageRoutedListener extends EventListener
{
    /**
     * Invoked when a message has been routed to the outbound queues for all target clients.
     * 
     * @param event
     *            The event containing the source message.
     */
    void messageRouted(MessageRoutedEvent event);
}
