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

import flex.messaging.MessageException;
import flex.messaging.log.LogEvent;

/**
 * @exclude
 */
public class FlexClientNotSubscribedException extends MessageException
{
    /**
     * @exclude
     */
    private static final long serialVersionUID = 773524927178340950L;

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------        
    
    //----------------------------------
    //  defaultLogMessageIntro
    //----------------------------------            

    /**
     * Overrides the intro text for the log message. 
     */
    @Override public String getDefaultLogMessageIntro()
    {
        return "FlexClient not subscribed: ";        
    }
    
    //----------------------------------
    //  logStackTraceEnabled
    //----------------------------------            
    
    /**
     * Override to disable stack trace logging.
     */
    @Override public boolean isLogStackTraceEnabled()
    {
        return false;        
    }    
    
    //----------------------------------
    //  peferredLogLevel
    //----------------------------------            
    
    /**
     * Override to lower the preferred log level to debug. 
     */
    @Override public short getPreferredLogLevel()
    {
        return LogEvent.DEBUG;        
    }
}
