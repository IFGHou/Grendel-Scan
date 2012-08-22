/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2008] Adobe Systems Incorporated
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

import java.util.EventObject;

import flex.messaging.messages.Message;

/**
 * @exclude
 * This event indicates that the source message has been routed to the outbound message queues
 * for all target clients.
 * This can be used as the trigger for performing optimized IO to flush these queued messages to 
 * remote hosts over the network.
 */
public class MessageRoutedEvent extends EventObject
{
    /**
     * @exclude
     */
    private static final long serialVersionUID = -3063794416424805005L;

    /**
     * Constructs a new <tt>MessageRoutedEvent</tt> using the supplied source <tt>Message</tt>.
     * 
     * @param message The message that has been routed.
     */
    public MessageRoutedEvent(Message message)
    {
        super(message);
    }
    
    /**
     * Returns the message that has been routed.
     */
    public Message getMessage()
    {
        return (Message)getSource();
    }
}
