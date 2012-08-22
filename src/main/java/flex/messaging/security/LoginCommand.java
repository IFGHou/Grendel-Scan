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
package flex.messaging.security;

import java.security.Principal;
import java.util.List;

import javax.servlet.ServletConfig;

/**
 * The class name of the implementation of this interface is configured in the
 * gateway configuration's security section and is instantiated using reflection
 * on servlet initialization.
 */
public interface LoginCommand
{
    /**
     * Called to initialize a login command prior to authentication/authorization requests.
     * 
     * @param config The servlet configuration for MessageBrokerServlet.  
     */
    void start(ServletConfig config);

    /**
     * Called to free up resources used by the login command.
     */
    void stop();

    /**
     * The gateway calls this method to perform programmatic, custom authentication.
     * <p>
     * The credentials are passed as a Map to allow for extra properties to be
     * passed in the future. For now, only a "password" property is sent.
     * </p>
     *
     * @param username    The principal being authenticated
     * @param credentials A map, typically with string keys and values - holds, for example, a password
     * @return principal for the authenticated user when authentication is successful; null otherwise 
     */
    Principal doAuthentication(String username, Object credentials);

    /**
     * The gateway calls this method to perform programmatic authorization.
     * <p>
     * A typical implementation would simply iterate over the supplied roles and
     * check that at least one of the roles returned true from a call to
     * HttpServletRequest.isUserInRole(String role).
     * </p>
     *
     * @param principal The principal being checked for authorization
     * @param roles    A List of role names to check, all members should be strings
     * @return true if the principal is authorized given the list of roles
     */
    boolean doAuthorization(Principal principal, List roles);

    /**
     * Attempts to log a user out from their session.
     *
     * NOTE: May not be possible on all application servers.
     * @param principal The principal to logout.
     * @return true when logout is successful
     */
    boolean logout(Principal principal);
}
