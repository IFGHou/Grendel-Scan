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
package flex.management;

import javax.management.MBeanServer;

/**
 * Interface for classes that locate MBeanServers to register MBeans with.
 * 
 * @author shodgson
 */
public interface MBeanServerLocator
{
    /**
     * Returns the MBeanServer to register our management MBeans with.
     * 
     * @return The MBeanServer to register our management MBeans with.
     */
    MBeanServer getMBeanServer();
    
}
