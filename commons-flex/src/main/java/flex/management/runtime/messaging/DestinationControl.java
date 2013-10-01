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
package flex.management.runtime.messaging;

import java.util.Date;

import javax.management.ObjectName;

import flex.management.BaseControl;
import flex.management.runtime.messaging.services.ServiceControl;
import flex.messaging.Destination;
import flex.messaging.services.ServiceAdapter;

/**
 * The <code>DestinationControl</code> class is the MBean implementation for monitoring and managing a <code>Destination</code> at runtime.
 * 
 * @author shodgson
 */
public abstract class DestinationControl extends BaseControl implements DestinationControlMBean
{
    protected Destination destination;
    private ObjectName adapter;

    /**
     * Constructs a new <code>DestinationControl</code> instance.
     * 
     * @param destination
     *            The <code>Destination</code> managed by this MBean.
     * @param parent
     *            The parent MBean in the management hierarchy.
     */
    public DestinationControl(Destination destination, BaseControl parent)
    {
        super(parent);
        this.destination = destination;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override
    public String getId()
    {
        return destination.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.DestinationControlMBean#getAdapter()
     */
    @Override
    public ObjectName getAdapter()
    {
        return adapter;
    }

    /**
     * Sets the <code>ObjectName</code> for the adapter associated with the managed destination.
     * 
     * @param value
     *            The <code>ObjectName</code> for the adapter.
     */
    public void setAdapter(ObjectName value)
    {
        adapter = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.DestinationControlMBean#isRunning()
     */
    @Override
    public Boolean isRunning()
    {
        return Boolean.valueOf(destination.isStarted());
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.DestinationControlMBean#getStartTimestamp()
     */
    @Override
    public Date getStartTimestamp()
    {
        return startTimestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    @Override
    public void preDeregister() throws Exception
    {
        ServiceControl parent = (ServiceControl) getParentControl();
        parent.removeDestination(getObjectName());

        // Unregister adapter of the destination
        ServiceAdapter child = destination.getAdapter();
        if (child.getControl() != null)
        {
            child.getControl().unregister();
            child.setControl(null);
            child.setManaged(false);
        }

        super.preDeregister();
    }

}
