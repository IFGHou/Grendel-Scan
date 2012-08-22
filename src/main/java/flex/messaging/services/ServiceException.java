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
package flex.messaging.services;

import flex.messaging.MessageException;
import flex.messaging.log.LogEvent;

/**
 * Exception type for Service errors.
 *
 * @author shodgson
 * @exclude
 */
public class ServiceException extends MessageException
{
    static final long serialVersionUID = 3349730139522030203L;
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------        
    
    //----------------------------------
    //  defaultLogMessageIntro
    //----------------------------------            

    /**
     * Overrides the intro text if the exception is a 'not subscribed' fault. 
     */
    @Override public String getDefaultLogMessageIntro()
    {
        if (code != null && code.equals(MessageService.NOT_SUBSCRIBED_CODE))
            return "Client not subscribed: ";
        else
            return super.getDefaultLogMessageIntro();
    }
    
    //----------------------------------
    //  logStackTraceEnabled
    //----------------------------------            
    
    /**
     * Override to disable stack trace logging if the exception is a 'not subscribed' fault. No need for
     * a stack trace in this case.
     */
    @Override public boolean isLogStackTraceEnabled()
    {
        if (code != null && code.equals(MessageService.NOT_SUBSCRIBED_CODE))
            return false;
        else
            return true;
    }    
    
    //----------------------------------
    //  peferredLogLevel
    //----------------------------------            
    
    /**
     * Override to lower the preferred log level to debug if the exception is a 'not subscribed' fault. 
     */
    @Override public short getPreferredLogLevel()
    {
        String code = getCode();
        // Log not-subscribed errors at a lower level because this is a common occurance
        // following normal failover.
        if (code != null && code.equals(MessageService.NOT_SUBSCRIBED_CODE))
            return LogEvent.DEBUG;
        else
            return super.getPreferredLogLevel();
    }
}