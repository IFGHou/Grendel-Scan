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

import java.util.Date;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.messaging.endpoints.BaseStreamingHTTPEndpoint;

/**
 * The <code>StreamingEndpointControl</code> class is the base MBean implementation for monitoring and managing a <code>BaseStreamingHTTPEndpoint</code> at runtime.
 */
public abstract class StreamingEndpointControl extends EndpointControl implements StreamingEndpointControlMBean
{
    private int pushCount;
    private Date lastPushTimeStamp;
    private long pushStart;

    /**
     * Constructs a <code>StreamingEndpointControl</code>, assigning managed message endpoint and parent MBean.
     * 
     * @param endpoint
     *            The <code>BaseStreamingHTTPEndpoint</code> managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public StreamingEndpointControl(BaseStreamingHTTPEndpoint endpoint, BaseControl parent)
    {
        super(endpoint, parent);
    }

    @Override
    protected void onRegistrationComplete()
    {
        super.onRegistrationComplete();

        String name = this.getObjectName().getCanonicalName();
        String[] generalPollables = { "LastPushTimestamp", "PushCount", "PushFrequency", "StreamingClientsCount" };

        getRegistrar().registerObjects(AdminConsoleTypes.ENDPOINT_POLLABLE, name, generalPollables);
        getRegistrar().registerObject(AdminConsoleTypes.ENDPOINT_SCALAR, name, "MaxStreamingClients");
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#getMaxStreamingClients()
     */
    @Override
    public Integer getMaxStreamingClients()
    {
        int maxStreamingClientsCount = ((BaseStreamingHTTPEndpoint) endpoint).getMaxStreamingClients();
        return new Integer(maxStreamingClientsCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#getPushCount()
     */
    @Override
    public Integer getPushCount()
    {
        return new Integer(pushCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#resetPushCount()
     */
    @Override
    public void resetPushCount()
    {
        pushStart = System.currentTimeMillis();
        pushCount = 0;
        lastPushTimeStamp = null;
    }

    /**
     * Increments the count of messages pushed by the endpoint.
     */
    public void incrementPushCount()
    {
        ++pushCount;
        lastPushTimeStamp = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#getLastPushTimestamp()
     */
    @Override
    public Date getLastPushTimestamp()
    {
        return lastPushTimeStamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#getPushFrequency()
     */
    @Override
    public Double getPushFrequency()
    {
        if (pushCount > 0)
        {
            double runtime = differenceInMinutes(pushStart, System.currentTimeMillis());
            return new Double(pushCount / runtime);
        }
        else
        {
            return new Double(0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean#isRunning()
     */
    @Override
    public Integer getStreamingClientsCount()
    {
        int streamingClientsCount = ((BaseStreamingHTTPEndpoint) endpoint).getStreamingClientsCount();
        return new Integer(streamingClientsCount);
    }
}
