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

import flex.management.BaseControl;
import flex.messaging.services.MessageService;

/**
 * The <code>MessageServiceControl</code> class is the MBean implemenation
 * for monitoring and managing a <code>MessageService</code> at runtime.
 *
 * @author shodgson
 */
public class MessageServiceControl extends ServiceControl implements
        MessageServiceControlMBean
{
    private static final String TYPE = "MessageService";

    /**
     * Constructs a <code>MessageServiceControl</code>, assigning its id, managed
     * message service and parent MBean.
     *
     * @param service The <code>MessageService</code> managed by this MBean.
     * @param parent The parent MBean in the management hierarchy.
     */
    public MessageServiceControl(MessageService service, BaseControl parent)
    {
        super(service, parent);
    }

    /** {@inheritDoc} */
    @Override public String getType()
    {
        return TYPE;
    }

}
