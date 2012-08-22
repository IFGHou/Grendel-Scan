/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
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
package flex.messaging.services.messaging.adapters;

import java.util.EventListener;

/**
 * An interface to be notified when a JMS message is received by the JMS
 * consumer. Implementations of this interface may add themselves as listeners
 * via <code>JMSConsumer.addJMSMessageListener</code>.
 */
public interface JMSMessageListener extends EventListener
{
    /**
     * Notification that a JMS message was received.
     * 
     * @param evt JMSMessageEvent to dispatch.
     */        
    public void messageReceived(JMSMessageEvent evt); 
}