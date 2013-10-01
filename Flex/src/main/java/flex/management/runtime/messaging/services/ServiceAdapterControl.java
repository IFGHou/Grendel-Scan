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
package flex.management.runtime.messaging.services;

import java.util.Date;

import flex.management.BaseControl;
import flex.management.runtime.messaging.DestinationControl;
import flex.messaging.services.ServiceAdapter;

/**
 * The <code>ServiceAdapterControl</code> class is the base MBean implementation 
 * for monitoring and managing a <code>ServiceAdapter</code> at runtime.
 * 
 * @author shodgson
 */
public abstract class ServiceAdapterControl extends BaseControl implements
        ServiceAdapterControlMBean
{
    protected ServiceAdapter serviceAdapter;  

    /**
     * Constructs a <code>ServiceAdapterControl</code>, assigning its id, managed service 
     * adapter and parent MBean.
     * 
     * @param serviceAdapter The <code>ServiceAdapter</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public ServiceAdapterControl(ServiceAdapter serviceAdapter, BaseControl parent)
    {
        super(parent);    
        this.serviceAdapter = serviceAdapter;  
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.BaseControlMBean#getId()
     */
    @Override public String getId()
    {
        return serviceAdapter.getId();
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ServiceAdapterControlMBean#isRunning()
     */
    @Override public Boolean isRunning()
    {
        return Boolean.valueOf(serviceAdapter.isStarted());
    }
    
    /*
     *  (non-Javadoc)
     * @see flex.management.runtime.ServiceAdapterControlMBean#getStartTimestamp()
     */
    @Override public Date getStartTimestamp()
    {
        return startTimestamp;
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    @Override public void preDeregister() throws Exception
    {
        DestinationControl parent = (DestinationControl)getParentControl();
        parent.setAdapter(null);
       
        super.preDeregister();
    }
}
