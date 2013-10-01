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
package flex.messaging.services.messaging.adapters;

import flex.messaging.FlexContext;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.ConfigurationConstants;
import flex.messaging.config.SecurityConstraint;
import flex.messaging.config.SecuritySettings;
import flex.messaging.security.LoginManager;
import flex.messaging.security.SecurityException;

/**
 * Messaging security constraint managers are used by messaging destinations to assert authorization of send and subscribe operations.
 */
public final class MessagingSecurityConstraintManager
{
    private static final String SEND_SECURITY_CONSTRAINT = "send-security-constraint";
    private static final String SUBSCRIBE_SECURITY_CONSTRAINT = "subscribe-security-constraint";

    private static final int NO_SEC_CONSTRAINT = 10062;

    private LoginManager loginManager;
    private SecuritySettings securitySettings;

    private SecurityConstraint sendConstraint;
    private SecurityConstraint subscribeConstraint;

    /**
     * Creates a new <code>MessagingSecurityConstraintManager</code> instance.
     * 
     * @param broker
     *            Associated <code>MessageBroker</code>.
     */
    public MessagingSecurityConstraintManager(MessageBroker broker)
    {
        this.loginManager = broker.getLoginManager();
        this.securitySettings = broker.getSecuritySettings();
    }

    /**
     * Sets the send constraint which is used when to assert authorization when sending a message.
     * 
     * @param ref
     *            The reference id of the constraint
     */
    public void setSendConstraint(String ref)
    {
        validateConstraint(ref);
        sendConstraint = securitySettings.getConstraint(ref);
    }

    /**
     * Sets the subscribe constraint which is used to assert authorization in subscribe, multi-subscribe, and unsubscribe operations.
     * 
     * @param ref
     *            The reference id of the constraint
     */
    public void setSubscribeConstraint(String ref)
    {
        validateConstraint(ref);
        subscribeConstraint = securitySettings.getConstraint(ref);
    }

    /**
     * @exclude Asserts send authorizations.
     */
    public void assertSendAuthorization()
    {
        checkConstraint(sendConstraint);
    }

    /**
     * @exclude Asserts subscribe authorizations.
     */
    public void assertSubscribeAuthorization()
    {
        checkConstraint(subscribeConstraint);
    }

    /**
     * @exclude Creates security constraints from the given server settings.
     * 
     * @param serverSettings
     *            The <code>ConfigMap</code> of server settings.
     */
    public void createConstraints(ConfigMap serverSettings)
    {
        // count constraint
        ConfigMap send = serverSettings.getPropertyAsMap(SEND_SECURITY_CONSTRAINT, null);
        if (send != null)
        {
            String ref = send.getPropertyAsString(ConfigurationConstants.REF_ATTR, null);
            if (ref != null)
                sendConstraint = securitySettings.getConstraint(ref);
        }

        // create constraint
        ConfigMap subscribe = serverSettings.getPropertyAsMap(SUBSCRIBE_SECURITY_CONSTRAINT, null);
        if (subscribe != null)
        {
            String ref = subscribe.getPropertyAsString(ConfigurationConstants.REF_ATTR, null);
            if (ref != null)
                subscribeConstraint = securitySettings.getConstraint(ref);
        }
    }

    private void checkConstraint(SecurityConstraint constraint)
    {
        if (constraint != null && !FlexContext.isMessageFromPeer())
        {
            try
            {
                loginManager.checkConstraint(constraint);
            }
            catch (SecurityException e)
            {
                throw e;
            }
        }
    }

    private void validateConstraint(String ref)
    {
        // If an attempt is made to use a constraint that we do not know about,
        // do not let the authorization succeed.
        if (securitySettings.getConstraint(ref) == null)
        {
            // Security constraint {0} is not defined.
            SecurityException se = new SecurityException();
            se.setMessage(NO_SEC_CONSTRAINT, new Object[] { ref });
            throw se;
        }
    }
}
