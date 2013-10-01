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
package flex.messaging;

/**
 * Interface to be notified when a FlexSession is created or destroyed. Implementations of this interface may add themselves as session created listeners statically via
 * <code>FlexSession.addSessionCreatedListener()</code>. To listen for FlexSession destruction, the implementation class instance must add itself as a listener to a specific FlexSession instance via
 * the <code>addSessionDestroyedListener()</code> method.
 */
public interface FlexSessionListener
{
    /**
     * Notification that a FlexSession was created.
     * 
     * @param session
     *            The FlexSession that was created.
     */
    void sessionCreated(FlexSession session);

    /**
     * Notification that a FlexSession is about to be destroyed.
     * 
     * @param session
     *            The FlexSession that will be destroyed.
     */
    void sessionDestroyed(FlexSession session);
}
