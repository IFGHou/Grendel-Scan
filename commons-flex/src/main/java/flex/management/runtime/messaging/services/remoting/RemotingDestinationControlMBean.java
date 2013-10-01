/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.management.runtime.messaging.services.remoting;

import java.io.IOException;

import flex.management.runtime.messaging.DestinationControlMBean;

/**
 * Defines the runtime monitoring and management interface for managed <code>RemotingDestination</code>s.
 * 
 * @author shodgson
 */
public interface RemotingDestinationControlMBean extends DestinationControlMBean
{
    /**
     * Returns the count of successful invocations for the destination.
     * 
     * @return The number of successful invocations for the destination.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getInvocationSuccessCount() throws IOException;

    /**
     * Returns the count of faulted invocations for the destination.
     * 
     * @return The number of successful invocations for the destination.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getInvocationFaultCount() throws IOException;

    /**
     * Returns the average invocation processing time in milliseconds for the destination.
     * 
     * @return The average invocation processing time in milliseconds for the destination.
     * @throws IOException
     *             Throws IOException.
     */
    Integer getAverageInvocationProcessingTimeMillis() throws IOException;
}
