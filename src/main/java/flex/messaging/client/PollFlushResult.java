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

/**
 * Extends <tt>FlushResult</tt> and adds additional properties for controlling 
 * client polling behavior.
 */
public class PollFlushResult extends FlushResult
{
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  avoidBusyPolling
    //----------------------------------

    private boolean avoidBusyPolling;
    
    /**
     * Indicates whether the handling of this result should attempt to avoid
     * potential busy-polling cycles.
     * This will be set to <code>true</code> in the case of two clients that are both
     * long-polling the server over the same session.
     * 
     * @return <code>true</code> if the handling of this result should attempt to avoid potential
     *         busy-polling cycles.
     */
    public boolean isAvoidBusyPolling()
    {
        return avoidBusyPolling;
    }
    
    /**
     * Set to <code>true</code> to signal that handling for this result should attempt to avoid
     * potential busy-polling cycles.
     * 
     * @param value <code>true</code> to signal that handling for this result should attempt to 
     *        avoid potential busy-polling cycles.
     */
    public void setAvoidBusyPolling(boolean value)
    {
        avoidBusyPolling = value;
    }
    
    //----------------------------------
    //  clientProcessingSuppressed
    //----------------------------------

    private boolean clientProcessingSuppressed;
    
    /**
     * Indicates whether client processing of this result should be
     * suppressed.
     * This should be <code>true</code> for results generated for poll requests
     * that arrive while a long-poll request from the same client is being serviced
     * to avoid a busy polling cycle.
     * 
     * @return <code>true</code> if client processing of this result should be suppressed;
     *         otherwise <code>false</code>.
     */
    public boolean isClientProcessingSuppressed()
    {
        return clientProcessingSuppressed;
    }
    
    /**
     * Set to <code>true</code> to suppress client processing of this result.
     * Default is <code>false</code>.
     * This should be set to <code>true</code> for results generated for poll requests
     * that arrive while a long-poll request from the same client is being serviced
     * to avoid a busy polling cycle.
     * 
     * @param value <code>true</code> to suppress client processing of the result.
     */
    public void setClientProcessingSuppressed(boolean value)
    {
        clientProcessingSuppressed = value;
    }
}
