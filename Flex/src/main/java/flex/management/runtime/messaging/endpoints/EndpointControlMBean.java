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
package flex.management.runtime.messaging.endpoints;

import java.io.IOException;
import java.util.Date;

import flex.management.BaseControlMBean;

/**
 * Defines the runtime monitoring and management interface for managed endpoints.
 *
 * @author shodgson
 */
public interface EndpointControlMBean extends BaseControlMBean
{
    /**
     * Returns <code>true</code> if the <code>Endpoint</code> is running.
     *
     * @return <code>true</code> if the <code>Endpoint</code> is running.
     * @throws IOException Throws IOException.
     */
    Boolean isRunning() throws IOException;

    /**
     * Returns the start timestamp for the <code>Endpoint</code>.
     *
     * @return The start timestamp for the <code>Endpoint</code>.
     * @throws IOException Throws IOException.
     */
    Date getStartTimestamp() throws IOException;

    /**
     * Returns the count of messages decoded by this endpoint and routed to the broker.
     *
     * @return The count of messages decoded by this endpoint and routed to the broker.
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
     * Returns the timestamp for the most recent message decoded by this endpoint and
     * routed to the broker.
     *
     * @return The timestamp for the most recent message decoded by this endpoint and
     * routed to the broker.
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
     * Returns the URI that corresponds to this endpoint.
     *
     * @return The URI that corresponds to this endpoint.
     * @throws IOException Throws IOException.
     */
    String getURI() throws IOException;

    /**
     * Returns the security constraint that is associated with this endpoint.
     *
     * @return The security constraint that is associated with this endpoint.
     * @throws IOException Throws IOException.
     */
    String getSecurityConstraint() throws IOException;

    /**
     * Returns the total Bytes that have been deserialized by this endpoint
     * during its lifetime.
     *
     * @return total Bytes deserialized.
     * @throws IOException Throws IOException.
     */
    Long getBytesDeserialized() throws IOException;

    /**
     * Returns the total Bytes that have been serialized by this endpoint
     * during its lifetime.
     *
     * @return total Bytes serialized.
     * @throws IOException Throws IOException.
     */
    Long getBytesSerialized() throws IOException;
}
