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
package flex.messaging.security;

import flex.messaging.services.messaging.Subtopic;

/**
 * This is an interface which can be implemented by the MessageAdapter or
 * by the DataManagement Assembler instance.  If it is implemented, this
 * class is used to do security filtering of subscribe and send operations.
 */
public interface MessagingSecurity 
{
    /**
     * This method is invoked before a client subscribe request is processed,
     * so that custom application logic can determine whether the client
     * should be allowed to subscribe to the specified subtopic. You can access 
     * the current user via
     * <code>FlexContext.getUserPrincipal()</code>.
     * 
     * @param subtopic The subtopic the client is attempting to subscribe to.
     * @return true to allow the subscription, false to prevent it.
     */
    boolean allowSubscribe(Subtopic subtopic);
    
    /**
     * This method is invoked before a client message is sent to a subtopic,
     * so that custom application logic can determine whether the client
     * should be allowed to send to the specified subtopic. You can access 
     * the current user via
     * <code>FlexContext.getUserPrincipal()</code>.
     * 
     * @param subtopic The subtopic the client is attempting to send a message to.
     * @return true to allow the message to be sent, false to prevent it.
     */
    boolean allowSend(Subtopic subtopic);
}
