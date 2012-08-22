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

import java.util.ArrayList;

import flex.messaging.messages.Message;

/**
 * @exclude
 * Supports registration and notification of <tt>MessageRoutedListener</tt>s.
 * An instance of this class is exposed by <tt>FlexContext</tt> while a message is
 * being routed, and once routing of the message to the outbound messages queues for
 * target clients and registered listeners are notified.
 * This class performs no synchronization because it is only used within the context
 * of a single Thread, and only during the routing of a single message.
 */
public class MessageRoutedNotifier
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     * Constructs a <tt>MessageRoutedNotifier</tt> for the supplied source message.
     * 
     * @param The source message being routed.
     */
    public MessageRoutedNotifier(Message message)
    {
        this.message = message;
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------
    
    /**
     * The source message being routed.
     */
    private final Message message;
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  messageRoutedListeners
    //----------------------------------
    
    private ArrayList listeners;
    

// TODO UCdetector: Remove unused code: 
//     /**
//      * Adds a <tt>MessageRoutedListener</tt>.
//      */
//     public void addMessageRoutedListener(MessageRoutedListener listener)
//     {
//         if (listener != null)
//         {
//             // Lazy-init only if necessary.
//             if (listeners == null)
//                 listeners = new ArrayList();
//             
//             // Add if absent.
//             if (!listeners.contains(listener))
//                 listeners.add(listener);
//         }
//     }
    

// TODO UCdetector: Remove unused code: 
//     /**
//      * Removes a <tt>MessageRoutedListener</tt>.
//      */
//     public void removeMessageRoutedListener(MessageRoutedListener listener)
//     {
//         if ((listener != null) && (listeners != null))
//             listeners.remove(listener);
//     }
    
    //--------------------------------------------------------------------------
    //
    // Public Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Notifies registered listeners of a routed message.
     * 
     * @param message The message that has been routed.
     */
    public void notifyMessageRouted()
    {
        if ((listeners != null) && !listeners.isEmpty())
        {
            MessageRoutedEvent event = new MessageRoutedEvent(message);
            int n = listeners.size();
            for (int i = 0; i < n; ++i)
                ((MessageRoutedListener)listeners.get(i)).messageRouted(event);
        }        
    }
}
