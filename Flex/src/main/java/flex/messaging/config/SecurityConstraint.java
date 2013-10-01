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
package flex.messaging.config;

import java.util.List;

/**
 * Security constraints are used by the login manager to secure access to
 * destinations and endpoints.
 *
 * @author Peter Farland
 */
public class SecurityConstraint
{
    /**
     * String constant for basic authentication.
     */
    public static final String BASIC_AUTH_METHOD = "Basic";

    /**
     * String constant for custom authentication.
     */
    public static final String CUSTOM_AUTH_METHOD = "Custom";

    private final String id;
    private String method;
    private List roles;

    /**
     * Creates an anonymous <code>SecurityConstraint</code> instance.
     */
    public SecurityConstraint()
    {
        this(null);
    }

    /**
     * Creates a <code>SecurityConstraint</code> instance with an id.
     *
     * @param id The id of the <code>SecurityConstraint</code> instance.
     */
    public SecurityConstraint(String id)
    {
        this.id = id;
        method = CUSTOM_AUTH_METHOD;
    }

    /**
     * Returns a list of roles of the <code>SecurityConstraint</code>.
     *
     * @return List of roles.
     */
    public List getRoles()
    {
        return roles;
    }


// TODO UCdetector: Remove unused code: 
//     /**
//      * Adds a role to the list of roles of the <code>SecurityConstraint</code>.
//      *
//      * @param role New role to add to the list of roles.
//      */
//     public void addRole(String role)
//     {
//         if (role != null)
//         {
//             if (roles == null)
//                 roles = new Vector();
// 
//             roles.add(role);
//         }
//     }

    /**
     * Returns the id of the <code>SecurityConstraint</code>.
     *
     * @return The id of the <code>SecurityConstraint</code>.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the authorization method of the <code>SecurityConstraint</code>.
     *
     * @return Authorization method.
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * Sets the authorization method of the <code>SecurityConstraint</code>.
     * Valid values are Basic and Custom.
     *
     * @param method The authentication method to set which can be custom or basic.
     */
    public void setMethod(String method)
    {
        if (method != null)
        {
            if (CUSTOM_AUTH_METHOD.equalsIgnoreCase(method))
            {
                this.method = CUSTOM_AUTH_METHOD;
            }
            else if (BASIC_AUTH_METHOD.equalsIgnoreCase(method))
            {
                this.method = BASIC_AUTH_METHOD;
            }
        }
    }
}
