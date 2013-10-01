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

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.management.runtime.messaging.DestinationControl;
import flex.messaging.services.remoting.RemotingDestination;

/**
 * The <code>RemotingDestinationControl</code> class is the MBean implementation for monitoring and managing a <code>RemotingDestination</code> at runtime.
 * 
 * This class performs no internal synchronization, so the statistics it tracks may differ slightly from the true values but they don't warrant the cost full synchronization.
 * 
 * @author shodgson
 */
public class RemotingDestinationControl extends DestinationControl implements RemotingDestinationControlMBean
{
    private static final String TYPE = "RemotingDestination";

    /**
     * Constructs a new <code>RemotingDestinationControl</code> instance.
     * 
     * @param destination
     *            The <code>RemotingDestination</code> managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public RemotingDestinationControl(RemotingDestination destination, BaseControl parent)
    {
        super(destination, parent);
    }

    private int invocationSuccessCount = 0;
    private int invocationFaultCount = 0;
    private int totalProcessingTimeMillis = 0;
    private int averageProcessingTimeMillis = 0;

    /** {@inheritDoc} */
    @Override
    public String getType()
    {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public Integer getInvocationSuccessCount() throws IOException
    {
        return Integer.valueOf(invocationSuccessCount);
    }

    /** Increment the invocation success count for the destination. */
    public void incrementInvocationSuccessCount(int processingTimeMillis)
    {
        try
        {
            invocationSuccessCount++;
            totalProcessingTimeMillis += processingTimeMillis;
            averageProcessingTimeMillis = totalProcessingTimeMillis / (invocationSuccessCount + invocationFaultCount);
        }
        catch (Exception needsReset)
        {
            reset();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer getInvocationFaultCount() throws IOException
    {
        return Integer.valueOf(invocationFaultCount);
    }

    /** Increment the invocation fault count for the destination. */
    public void incrementInvocationFaultCount(int processingTimeMillis)
    {
        try
        {
            invocationFaultCount++;
            totalProcessingTimeMillis += processingTimeMillis;
            averageProcessingTimeMillis = totalProcessingTimeMillis / (invocationSuccessCount + invocationFaultCount);
        }
        catch (Exception needsReset)
        {
            reset();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer getAverageInvocationProcessingTimeMillis() throws IOException
    {
        return Integer.valueOf(averageProcessingTimeMillis);
    }

    /**
     * Callback used to register properties for display in the admin application.
     */
    @Override
    protected void onRegistrationComplete()
    {
        String name = this.getObjectName().getCanonicalName();

        String[] pollablePerInterval = { "InvocationSuccessCount", "InvocationFaultCount", "AverageInvocationProcessingTimeMillis" };

        getRegistrar().registerObjects(new int[] { AdminConsoleTypes.DESTINATION_POLLABLE, AdminConsoleTypes.GRAPH_BY_POLL_INTERVAL }, name, pollablePerInterval);
    }

    /**
     * Helper method to reset state in the case of errors updating statistics.
     */
    private void reset()
    {
        invocationSuccessCount = 0;
        invocationFaultCount = 0;
        totalProcessingTimeMillis = 0;
        averageProcessingTimeMillis = 0;
    }
}
