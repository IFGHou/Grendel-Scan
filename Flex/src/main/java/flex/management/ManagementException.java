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

import flex.messaging.MessageException;

/**
 * This exception type is thrown when errors occur generating 
 * <code>ObjectName</code>s for MBeans, or when registering them or
 * accessing them via the MBean server.
 * 
 * @author shodgson
 */
public class ManagementException extends MessageException
{
    // Inherits all functionality from MessageException.
    static final long serialVersionUID = 1296149563830613956L;
}
