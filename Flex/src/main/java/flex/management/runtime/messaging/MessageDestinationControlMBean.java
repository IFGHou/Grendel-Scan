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
package flex.management.runtime.messaging;

import java.io.IOException;
import java.util.Date;

import javax.management.ObjectName;


/**
 * Defines the runtime monitoring and management interface for managed
 * <code>MessageDestination</code>s.
 *
 * @author shodgson
 */
public interface MessageDestinationControlMBean extends DestinationControlMBean
{
    /**
     * Returns the <code>ObjectName</code> for the message cache used by the managed
     * destination.
     *
     * @return The <code>ObjectName</code> for the message cache.
     * @throws IOException Throws IOException.
     */
    ObjectName getMessageCache() throws IOException;

    /**
     * Returns the <code>ObjectName</code> for the throttle manager used by the
     * managed destination.
     *
     * @return The <code>ObjectName</code> for the throttle manager.
     * @throws IOException Throws IOException.
     */
    ObjectName getThrottleManager() throws IOException;

    /**
     * Returns the <code>ObjectName</code> for the subscription manager used
     * by the managed destination.
     *
     * @return The <code>ObjectName</code> for the subscription manager.
     * @throws IOException Throws IOException.
     */
    ObjectName getSubscriptionManager() throws IOException;

    /**
     * Returns the number of service message invocations.
     *
     * @return The number of service message invocations.
     * @throws IOException Throws IOException.
     */
    Integer getServiceMessageCount() throws IOException;

    /**
     * Resets the count of service message invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetServiceMessageCount() throws IOException;

    /**
     * Returns the timestamp for the most recent service message
     * invocation.
     *
     * @return The timestamp for the most recent service message invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastServiceMessageTimestamp() throws IOException;

    /**
     * Returns the number of service message invocations per minute.
     *
     * @return The number of service message invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getServiceMessageFrequency() throws IOException;

    /**
     * Returns the number of service command invocations.
     *
     * @return The number of service command invocations.
     * @throws IOException Throws IOException.
     */
    Integer getServiceCommandCount() throws IOException;

    /**
     * Resets the count of service command invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetServiceCommandCount() throws IOException;

    /**
     * Returns the timestamp for the most recent service command invocation.
     *
     * @return The timestamp for the most recent service command invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastServiceCommandTimestamp() throws IOException;

    /**
     * Returns the number of service command invocations per minute.
     *
     * @return The number of service command invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getServiceCommandFrequency() throws IOException;

    /**
     * Returns the number of messages from an adapter that the managed service
     * has processed.
     *
     * @return The number of messages from an adapter that the managed service
     * has processed
     * @throws IOException Throws IOException.
     */
    Integer getServiceMessageFromAdapterCount() throws IOException;

    /**
     * Resets the count of service message from adapter invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetServiceMessageFromAdapterCount() throws IOException;

    /**
     * Returns the timestamp of the most recent service message from adapter invocation.
     *
     * @return The timestamp of the most recent service message from adapter invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastServiceMessageFromAdapterTimestamp() throws IOException;

    /**
     * Returns the number of service message from adapter invocations per minute.
     *
     * @return The number of service message from adapter invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getServiceMessageFromAdapterFrequency() throws IOException;
}
