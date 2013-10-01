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
package flex.messaging.services.remoting.adapters;

import flex.messaging.config.SecurityConstraint;

/**
 * Used to define included and excluded methods exposed by the <tt>JavaAdapter</tt> for a remoting destination. This class performs no internal synchronization.
 */
public class RemotingMethod
{
    // --------------------------------------------------------------------------
    //
    // Properties
    //
    // --------------------------------------------------------------------------

    // ----------------------------------
    // name
    // ----------------------------------

    private String name;

    /**
     * Returns the method name. Because mapping ActionScript data types to Java data types is indeterminate in some cases, explicit overloaded methods are not currently supported so no parameter
     * signature property is defined.
     * 
     * @return method name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the method name. Because mapping ActionScript data types to Java data types is indeterminate in some cases, explicit overloaded methods are not currently supported so no parameter
     * signature property is defined.
     * 
     * @param value
     *            method name
     */
    public void setName(String value)
    {
        name = value;
    }

    // ----------------------------------
    // securityConstraint
    // ----------------------------------

    private SecurityConstraint constraint;

    /**
     * Returns the <tt>SecurityConstraint</tt> that will be applied to invocations of the remoting method.
     * 
     * @return <tt>SecurityConstraint</tt> that will be applied to invocations of the remoting method.
     */
    public SecurityConstraint getSecurityConstraint()
    {
        return constraint;
    }

    /**
     * Sets the <tt>SecurityConstraint</tt> that will be applied to invocations of the remoting method.
     * 
     * @param value
     *            the <tt>SecurityConstraint</tt> that will be applied to invocations of the remoting method.
     */
    public void setSecurityConstraint(SecurityConstraint value)
    {
        constraint = value;
    }
}
