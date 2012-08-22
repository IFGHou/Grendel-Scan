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

import flex.management.BaseControl;
import flex.messaging.endpoints.StreamingAMFEndpoint;

/**
 * The <code>StreamingAMFEndpointControl</code> class is the MBean implemenation
 * for monitoring and managing an <code>StreamingAMFEndpoint</code> at runtime.
 */
public class StreamingAMFEndpointControl extends StreamingEndpointControl implements
        StreamingAMFEndpointControlMBean
{
    private static final String TYPE = "StreamingAMFEndpoint";

    /**
     * Constructs a <code>StreamingAMFEndpointControl</code>, assigning managed message
     * endpoint and parent MBean.
     *
     * @param endpoint The <code>StreamingAMFEndpoint</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public StreamingAMFEndpointControl(StreamingAMFEndpoint endpoint, BaseControl parent)
    {
        super(endpoint, parent);
    }

    /** {@inheritDoc} */
    @Override public String getType()
    {
        return TYPE;
    }
}
