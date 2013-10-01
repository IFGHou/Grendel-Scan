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

/**
 * Manageability of a class is enabled by implementing this interface. The specific level of manageability is defined by the relationship between a manageable component and its corresponding control.
 * 
 * @author shodgson
 */
public interface Manageable
{
    /**
     * Returns <code>true</code> if the component is enabled for management.
     * 
     * @return <code>true</code> if the component is enabled for management.
     */
    boolean isManaged();

    /**
     * Enables or disables management for the component.
     * 
     * @param enableManagement
     *            <code>true</code> to enable management, <code>false</code> to disable management.
     */
    void setManaged(boolean enableManagement);

    /**
     * Returns the control MBean used to manage the component.
     * 
     * @return The control MBean used to manage the component.
     */
    BaseControl getControl();

    /**
     * Set the control MBean used to manage the component.
     * 
     * @param control
     *            The <code>BaseControl</code> MBean used to manage the component.
     */
    void setControl(BaseControl control);
}
