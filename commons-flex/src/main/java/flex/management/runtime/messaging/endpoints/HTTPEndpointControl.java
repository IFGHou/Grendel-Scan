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
package flex.management.runtime.messaging.endpoints;

import flex.management.BaseControl;
import flex.messaging.endpoints.HTTPEndpoint;

/**
 * The <code>HTTPEndpointControl</code> class is the MBean implemenation for monitoring and managing a <code>HTTPEndpoint</code> at runtime.
 * 
 * @author shodgson
 */
public class HTTPEndpointControl extends EndpointControl implements HTTPEndpointControlMBean
{
    private static final String TYPE = "HTTPEndpoint";

    /**
     * Constructs a <code>HTTPEndpointControl</code>, assigning managed message endpoint and parent MBean.
     * 
     * @param endpoint
     *            The <code>HTTPEndpoint</code> managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public HTTPEndpointControl(HTTPEndpoint endpoint, BaseControl parent)
    {
        super(endpoint, parent);
    }

    /** {@inheritDoc} */
    @Override
    public String getType()
    {
        return TYPE;
    }
}
