/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.client;

/**
 * Used alongside invocations of <code>FlexClient.pollWithWait()</code> to allow calling code to maintain a record of the Objects being used to place waited poll requests into a wait state. This can
 * be used to break the threads out of their wait state separately from the internal waited poll handling within <code>FlexClient</code>.
 */
public interface PollWaitListener
{
    /**
     * Hook method invoked directly before a wait begins.
     * 
     * @param notifier
     *            The <tt>Object</tt> being used to <code>wait()/notify()</code>.
     */
    void waitStart(Object notifier);

    /**
     * Hook method invoked directly after a wait completes.
     * 
     * @param notifier
     *            The <tt>Object</tt> being used to <code>wait()/notify()</code>.
     */
    void waitEnd(Object notifier);
}
