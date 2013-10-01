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
package flex.management;

import java.io.IOException;

import javax.management.ObjectName;

/**
 * The base MBean interface for management beans that control aspects of Flex behavior on the server.
 * 
 * @author shodgson
 */
public interface BaseControlMBean
{
    /**
     * Returns the id for this MBean. This is the value that is set for the <code>id</code> key in the <code>ObjectName</code> for this MBean.
     * 
     * @return The MBean instance id.
     * @throws IOException
     *             Throws IOException.
     */
    String getId() throws IOException;

    /**
     * Returns the type for this MBean. This is the value that is set for the <code>type</code> key in the <code>ObjectName</code> for this MBean.
     * 
     * @return The MBean instance type.
     * @throws IOException
     *             Throws IOException.
     */
    String getType() throws IOException;

    /**
     * Returns the parent for this MBean. The value is the <code>ObjectName</code> for the parent MBean that conceptually contains this MBean instance. If no parent exists, this method returns
     * <code>null</code>.
     * 
     * @return The <code>ObjectName</code> for the parent of this MBean instance.
     * @throws IOException
     *             Throws IOException.
     */
    ObjectName getParent() throws IOException;
}
