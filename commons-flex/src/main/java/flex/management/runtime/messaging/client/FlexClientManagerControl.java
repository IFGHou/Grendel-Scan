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
package flex.management.runtime.messaging.client;

import flex.management.BaseControl;
import flex.management.runtime.AdminConsoleTypes;
import flex.messaging.client.FlexClientManager;

/**
 * @author majacobs
 * 
 * @exclude
 */
public class FlexClientManagerControl extends BaseControl implements FlexClientManagerControlMBean
{
    private FlexClientManager flexClientManager;

    public FlexClientManagerControl(BaseControl parent, FlexClientManager manager)
    {
        super(parent);
        flexClientManager = manager;
    }

    @Override
    public void onRegistrationComplete()
    {
        String name = getObjectName().getCanonicalName();
        getRegistrar().registerObject(AdminConsoleTypes.GENERAL_POLLABLE, name, "FlexClientCount");
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControl#getId()
     */
    @Override
    public String getId()
    {
        return flexClientManager.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.BaseControl#getType()
     */
    @Override
    public String getType()
    {
        return flexClientManager.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.client.FlexClientManagerControlMBean#getClientIds()
     */
    @Override
    public String[] getClientIds()
    {
        return flexClientManager.getClientIds();
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.client.FlexClientManagerControlMBean#getClientLastUse(java.lang.String)
     */
    @Override
    public Long getClientLastUse(String clientId)
    {
        return new Long(flexClientManager.getFlexClient(clientId).getLastUse());
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.client.FlexClientManagerControlMBean#getClientSessionCount(java.lang.String)
     */
    @Override
    public Integer getClientSessionCount(String clientId)
    {
        return new Integer(flexClientManager.getFlexClient(clientId).getSessionCount());
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.client.FlexClientManagerControlMBean#getClientSubscriptionCount(java.lang.String)
     */
    @Override
    public Integer getClientSubscriptionCount(String clientId)
    {
        return new Integer(flexClientManager.getFlexClient(clientId).getSubscriptionCount());
    }

    /*
     * (non-Javadoc)
     * 
     * @see flex.management.runtime.messaging.client.FlexClientManagerControlMBean#getFlexClientCount()
     */
    @Override
    public Integer getFlexClientCount()
    {
        return new Integer(flexClientManager.getFlexClientCount());
    }
}
