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

import java.io.IOException;
import java.util.Date;

import flex.management.BaseControlMBean;

/**
 * Defines the runtime monitoring and management interface for service adapters.
 *
 * @author shodgson
 */
public interface ServiceAdapterControlMBean extends BaseControlMBean
{
    /**
     * Returns <code>true</code> if the <code>ServiceAdapter</code> is running.
     *
     * @return <code>true</code> if the <code>ServiceAdapter</code> is running.
     * @throws IOException Throws IOException.
     */
    Boolean isRunning() throws IOException;

    /**
     * Returns the start timestamp for the <code>ServiceAdapter</code>.
     *
     * @return The start timestamp for the <code>ServiceAdapter</code>.
     * @throws IOException Throws IOException.
     */
    Date getStartTimestamp() throws IOException;

}
