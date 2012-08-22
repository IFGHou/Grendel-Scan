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
package flex.management.runtime.messaging.services.messaging;

import java.io.IOException;
import java.util.Date;

import flex.management.BaseControlMBean;

/**
 * Defines the runtime monitoring and management interface for
 * <code>ThrottleManager</code>s.
 *
 * @author shodgson
 */
public interface ThrottleManagerControlMBean extends BaseControlMBean
{
    /**
     * Returns the number of incoming client messages that have been
     * throttled.
     *
     * @return The number of incoming client messages that have been
     * throttled.
     * @throws IOException Throws IOException.
     */
    Integer getClientIncomingMessageThrottleCount() throws IOException;

    /**
     * Resets the number of throttled incoming client messages to 0.
     *
     * @throws IOException Throws IOException.
     */
    void resetClientIncomingMessageThrottleCount() throws IOException;

    /**
     * Returns the timestamp when an incoming client message was
     * most recently throttled.
     *
     * @return The timestamp when an incoming client message was
     * most recently throttled.
     * @throws IOException Throws IOException.
     */
    Date getLastClientIncomingMessageThrottleTimestamp() throws IOException;

    /**
     * Returns the number of incoming client messages that have been
     * throttled per minute.
     *
     * @return The number of incoming client messages that have been
     * throttled per minute.
     * @throws IOException Throws IOException.
     */
    Double getClientIncomingMessageThrottleFrequency() throws IOException;

    /**
     * Returns the number of outgoing client messages that have been
     * throttled.
     *
     * @return The number of outgoing client messages that have been
     * throttled.
     * @throws IOException Throws IOException.
     */
    Integer getClientOutgoingMessageThrottleCount() throws IOException;

    /**
     * Resets the number of throttled outgoing client messages to 0.
     *
     * @throws IOException Throws IOException.
     */
    void resetClientOutgoingMessageThrottleCount() throws IOException;

    /**
     * Returns the timestamp when an outgoing client message was most
     * recently throttled.
     *
     * @return The timestamp when an outgoing client message was most
     * recently throttled.
     * @throws IOException Throws IOException.
     */
    Date getLastClientOutgoingMessageThrottleTimestamp() throws IOException;

    /**
     * Returns the number of outgoing client messages that have been
     * throttled per minute.
     *
     * @return The number of outgoing client messages that have been
     * throttled per minute.
     * @throws IOException Throws IOException.
     */
    Double getClientOutgoingMessageThrottleFrequency() throws IOException;

    /**
     * Returns the number of incoming destination messages that have
     * been throttled.
     *
     * @return The number of incoming destination messages that have
     * been throttled.
     * @throws IOException Throws IOException.
     */
    Integer getDestinationIncomingMessageThrottleCount() throws IOException;

    /**
     * Resets the number of throttled incoming destination messages to 0.
     *
     * @throws IOException Throws IOException.
     */
    void resetDestinationIncomingMessageThrottleCount() throws IOException;

    /**
     * Returns the timestamp when an incoming destination message was
     * most recently throttled.
     *
     * @return The timestamp when an incoming destination message was
     * most recently throttled.
     * @throws IOException Throws IOException.
     */
    Date getLastDestinationIncomingMessageThrottleTimestamp() throws IOException;

    /**
     * Returns the number of incoming destination messages that have
     * been throttled per minute.
     *
     * @return The number of incoming destination messages that have
     * been throttled per minute.
     * @throws IOException Throws IOException.
     */
    Double getDestinationIncomingMessageThrottleFrequency() throws IOException;

    /**
     * Returns the number of outgoing destination messages that have
     * been throttled.
     *
     * @return The number of outgoing destination messages that have
     * been throttled.
     * @throws IOException Throws IOException.
     */
    Integer getDestinationOutgoingMessageThrottleCount() throws IOException;

    /**
     * Resets the number of throttled outgoing destination messages to 0.
     *
     * @throws IOException Throws IOException.
     */
    void resetDestinationOutgoingMessageThrottleCount() throws IOException;

    /**
     * Returns the timestamp when an outgoing destination message was
     * most recently throttled.
     *
     * @return The timestamp when an outgoing destination message was
     * most recently throttled.
     * @throws IOException Throws IOException.
     */
    Date getLastDestinationOutgoingMessageThrottleTimestamp() throws IOException;

    /**
     * Returns the number of outgoing destination messages that have been
     * throttled per minute.
     *
     * @return The number of outgoing destination messages that have been
     * throttled per minute.
     * @throws IOException Throws IOException.
     */
    Double getDestinationOutgoingMessageThrottleFrequency() throws IOException;
}
