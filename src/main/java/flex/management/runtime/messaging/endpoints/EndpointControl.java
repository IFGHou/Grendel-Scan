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

import java.util.Date;
import java.util.List;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.management.runtime.messaging.MessageBrokerControl;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.endpoints.Endpoint;

/**
 * The <code>EndpointControl</code> class is the MBean implementation for
 * monitoring and managing an <code>Endpoint</code> at runtime.
 *
 * @author shodgson
 */
public abstract class EndpointControl extends BaseControl implements EndpointControlMBean
{
    protected Endpoint endpoint;
    private int serviceMessageCount;
    private Date lastServiceMessageTimestamp;
    private long serviceMessageStart;
    private long bytesDeserialized=0;
    private long bytesSerialized=0;

    /**
     * Constructs an <code>EndpointControl</code>, assigning its managed endpoint and
     * parent MBean.
     *
     * @param endpoint The <code>Endpoint</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public EndpointControl(Endpoint endpoint, BaseControl parent)
    {
        super(parent);
        this.endpoint = endpoint;
        serviceMessageStart = System.currentTimeMillis();
    }


    @Override protected void onRegistrationComplete()
    {
        String name = this.getObjectName().getCanonicalName();
        String[] generalNames = { "SecurityConstraint"};
        String[] generalPollables = { "ServiceMessageCount", "LastServiceMessageTimestamp", "ServiceMessageFrequency"};
        String[] pollableGraphByInterval = {"BytesDeserialized", "BytesSerialized"};

        getRegistrar().registerObjects(AdminConsoleTypes.ENDPOINT_SCALAR,
                name, generalNames);
        getRegistrar().registerObjects(AdminConsoleTypes.ENDPOINT_POLLABLE,
                name, generalPollables);
        getRegistrar().registerObjects(new int[] {AdminConsoleTypes.GRAPH_BY_POLL_INTERVAL, AdminConsoleTypes.ENDPOINT_POLLABLE},
                name, pollableGraphByInterval);
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override public String getId()
    {
        return endpoint.getId();
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#isRunning()
     */
    @Override public Boolean isRunning()
    {
        return Boolean.valueOf(endpoint.isStarted());
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getStartTimestamp()
     */
    @Override public Date getStartTimestamp()
    {
        return startTimestamp;
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getServiceMessageCount()
     */
    @Override public Integer getServiceMessageCount()
    {
        return new Integer(serviceMessageCount);
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#resetServiceMessageCount()
     */
    @Override public void resetServiceMessageCount()
    {
        serviceMessageStart = System.currentTimeMillis();
        serviceMessageCount = 0;
        lastServiceMessageTimestamp = null;
    }

    /**
     * Increments the count of <code>serviceMessage()</code> invocations by the endpoint.
     */
    public void incrementServiceMessageCount()
    {
        ++serviceMessageCount;
        lastServiceMessageTimestamp = new Date();
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getLastServiceMessageTimestamp()
     */
    @Override public Date getLastServiceMessageTimestamp()
    {
        return lastServiceMessageTimestamp;
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getServiceMessageFrequency()
     */
    @Override public Double getServiceMessageFrequency()
    {
        if (serviceMessageCount > 0)
        {
            double runtime = differenceInMinutes(serviceMessageStart, System.currentTimeMillis());
            return new Double(serviceMessageCount/runtime);
        }
        else
        {
            return new Double(0);
        }
    }

    /*
     *  (non-Javadoc)
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    @Override public void preDeregister() throws Exception
    {
        MessageBrokerControl parent = (MessageBrokerControl)getParentControl();
        parent.removeEndpoint(getObjectName());
    }

    @Override public String getURI()
    {
        return endpoint.getUrl();
    }

    @Override public String getSecurityConstraint()
    {
        return getSecurityConstraintOf(endpoint);
    }

    public static String getSecurityConstraintOf(Endpoint endpoint)
    {
        String result = "None";

        SecurityConstraint constraint = endpoint.getSecurityConstraint();
        if (constraint != null)
        {
            String authMethod = constraint.getMethod();
            if (authMethod != null)
            {
                StringBuffer buffer = new StringBuffer();
                buffer.append(authMethod);

                List roles = constraint.getRoles();
                if ((roles != null) && !roles.isEmpty())
                {
                    buffer.append(':');
                    for (int i = 0; i < roles.size(); i++)
                    {
                        if (i > 0)
                        {
                            buffer.append(',');
                        }
                        buffer.append(' ');
                        buffer.append(roles.get(i));
                    }
                }
                result = buffer.toString();
            }
        }
        return result;
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getBytesDeserialized()
     */
    @Override public Long getBytesDeserialized(){
        return new Long(bytesDeserialized);
    }

    /**
     * Increments the count of bytes deserialized by the endpoint.
     */
    public void addToBytesDeserialized(int bytesDeserialized) {
        this.bytesDeserialized += bytesDeserialized;
    }

    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.EndpointControlMBean#getBytesSerialized()
     */
    @Override public Long getBytesSerialized() {
        return new Long(bytesSerialized);
    }

    /**
     * Increments the count of bytes serialized by the endpoint.
     */
    public void addToBytesSerialized(int bytesSerialized) {
        this.bytesSerialized += bytesSerialized;
    }
}
