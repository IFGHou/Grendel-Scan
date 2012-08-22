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
package flex.messaging;

import flex.management.runtime.messaging.MessageBrokerControl;

/**
 * Helper class used to track session metrics for a MessageBroker.
 *
 * @exclude
 */
public class SessionMetricsTracker implements FlexSessionListener
{
    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param messageBroker The owning MessageBroker instance.
     */
    public SessionMetricsTracker(MessageBroker messageBroker)
    {
        this.messageBroker = messageBroker;
    }

    //--------------------------------------------------------------------------
    //
    // Variables
    //
    //--------------------------------------------------------------------------

    /**
     * The current connection count.
     */
    private int connectionCount;

    /**
     * The local maximum for number of connections in the current measurement
     * timespan.
     */
    private int currentConnectionCountMax;

    /**
     * Reference to the owning MessageBroker instance.
     */
    private MessageBroker messageBroker;

    //--------------------------------------------------------------------------
    //
    // Methods
    //
    //--------------------------------------------------------------------------

    // implement FlexSessionListener
    @Override public synchronized void sessionCreated(FlexSession session)
    {
        session.addSessionDestroyedListener(this);
        ++connectionCount;
        if (connectionCount > currentConnectionCountMax)
            currentConnectionCountMax = connectionCount;
        if (messageBroker.isManaged())
        {
            ((MessageBrokerControl)messageBroker.getControl()).setFlexSessionCount(connectionCount);
            ((MessageBrokerControl)messageBroker.getControl()).setMaxFlexSessionsInCurrentHour(currentConnectionCountMax);
        }
    }

    // implement FlexSessionListener
    @Override public synchronized void sessionDestroyed(FlexSession session)
    {
        session.removeSessionDestroyedListener(this);
        --connectionCount;
        if (messageBroker.isManaged())
        {
            ((MessageBrokerControl)messageBroker.getControl()).setFlexSessionCount(connectionCount);
            ((MessageBrokerControl)messageBroker.getControl()).setMaxFlexSessionsInCurrentHour(currentConnectionCountMax);
        }
    }

    /**
     * Adds the session metrics tracker as a listener for session creation.
     */
    public synchronized void start()
    {
        FlexSession.addSessionCreatedListener(this);
    }

    /**
     * Removes the session metrics tracker as a listener for session creation.
     */
    public synchronized void stop()
    {
        FlexSession.removeSessionCreatedListener(this);
    }
}
