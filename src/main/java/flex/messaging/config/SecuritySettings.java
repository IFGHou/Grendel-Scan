/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  [2002] - [2007] Adobe Systems Incorporated
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

import java.util.HashMap;
import java.util.Map;

import flex.messaging.security.SecurityException;

/**
 * @author Peter Farland
 * @exclude
 */
public class SecuritySettings
{
    // Exception/error message numbers.
    private static final int NO_SEC_CONSTRAINT = 10062;

    private String serverInfo;
    private Map loginCommandSettings;
    private Map constraints;

    public SecuritySettings()
    {
        constraints = new HashMap();
        loginCommandSettings = new HashMap();
    }

/* TODO UCdetector: Remove unused code: 
    public void addConstraint(SecurityConstraint sc)
    {
        constraints.put(sc.getId(), sc);
    }
*/

    public SecurityConstraint getConstraint(String ref)
    {
        // If an attempt is made to use a constraint that we do not know about,
        // do not let the authorization succeed
        if (constraints.get(ref) == null)
        {
            // Security constraint {0} is not defined.
            SecurityException se = new SecurityException();
            se.setMessage(NO_SEC_CONSTRAINT, new Object[] {ref});
            throw se;
        }
        return (SecurityConstraint)constraints.get(ref);
    }

/* TODO UCdetector: Remove unused code: 
    public void addLoginCommandSettings(LoginCommandSettings lcs)
    {
        loginCommandSettings.put(lcs.getServer(), lcs);
    }
*/

    public Map getLoginCommands()
    {
        return loginCommandSettings;
    }

    public void setServerInfo(String s)
    {
        serverInfo = s;
    }

    public String getServerInfo()
    {
        return serverInfo;
    }
}
