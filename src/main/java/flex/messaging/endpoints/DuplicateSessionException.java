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
package flex.messaging.endpoints;

import flex.messaging.MessageException;
import flex.messaging.log.LogEvent;

/**
 * Exception class used to indicate duplicate client sessions were detected.
 */
public class DuplicateSessionException extends MessageException
{
    /**
     * @exclude
     */
    public static final String DUPLICATE_SESSION_DETECTED_CODE = "Server.Processing.DuplicateSessionDetected"; 
    
    /**
     * @exclude
     */
    private static final long serialVersionUID = -741704726700619666L;

    //--------------------------------------------------------------------------
    //
    // Constructors
    //
    //--------------------------------------------------------------------------    

    /**
     * Default constructor. 
     * Sets the code to a default value of <code>DUPLICATE_SESSION_DETECTED_CODE</code>.
     */
    public DuplicateSessionException()
    {
        setCode(DUPLICATE_SESSION_DETECTED_CODE);
    }
    
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------        
    
    //----------------------------------
    //  preferredLogLevel
    //----------------------------------            

    /**
     * Override to log at the DEBUG level.
     */
    @Override public short getPreferredLogLevel()
    {
        return LogEvent.DEBUG;
    }
    
    //----------------------------------
    //  logStackTraceEnabled
    //----------------------------------            
    
    /**
     * Override to suppress stack trace logging.
     */ 
    @Override public boolean isLogStackTraceEnabled()
    {
        return false;
    }    
}
