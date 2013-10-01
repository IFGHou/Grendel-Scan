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
 * An interface to be notified when a JMS exception is encountered by the JMS
 * consumer. Implementations of this interface may add themselves as listeners
 * via <code>JMSConsumer.addJMSExceptionListener</code>.
 */
public interface JMSExceptionListener extends EventListener
{
    /**
     * Notification that a JMS exception was encountered.
     * 
     * @param evt JMSExceptionEvent to dispatch.
     */    
    public void exceptionThrown(JMSExceptionEvent evt); 
}