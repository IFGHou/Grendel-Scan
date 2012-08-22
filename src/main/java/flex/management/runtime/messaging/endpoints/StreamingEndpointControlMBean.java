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

/**
 * Defines the runtime monitoring and management interface for managed streaming
 * endpoints.
 */
public interface StreamingEndpointControlMBean extends EndpointControlMBean
{
    /**
     * Returns the maximum number of clients that will be allowed to establish
     * a streaming HTTP connection with the endpoint.
     *
     * @return The maximum number of clients that will be allowed to establish
     * a streaming HTTP connection with the endpoint.
     * @throws IOException Throws IOException.
     */
    Integer getMaxStreamingClients() throws IOException;

    /**
     * Returns the count of push invocations.
     *
     * @return The count of push invocations.
     * @throws IOException Throws IOException.
     */
    Integer getPushCount() throws IOException;

    /**
     * Resets the count of push invocations.
     *
     * @throws IOException Throws IOException.
     */
    void resetPushCount() throws IOException;

    /**
     * Returns the timestamp for the most recent push invocation.
     *
     * @return The timestamp for the most recent push invocation.
     * @throws IOException Throws IOException.
     */
    Date getLastPushTimestamp() throws IOException;

    /**
     * Returns the number of push invocations per minute.
     *
     * @return The number of push invocations per minute.
     * @throws IOException Throws IOException.
     */
    Double getPushFrequency() throws IOException;

    /**
     * Returns the the number of clients that are currently in the streaming state.
     *
     * @return The number of clients that are currently in the streaming state.
     * @throws IOException Throws IOException.
     */
    Integer getStreamingClientsCount() throws IOException;
}
