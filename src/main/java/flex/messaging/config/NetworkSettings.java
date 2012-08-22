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

/**
 * Network policy settings for a MessageDestination.
 *
 * @author neville
 */
public class NetworkSettings
{
    protected String clusterId;
    protected ThrottleSettings throttleSettings;
    protected int subscriptionTimeoutMinutes;
    // This is the more common case so make it the default
    protected boolean sharedBackend = true;
    private boolean sharedBackendSet = false;

    public static final String NETWORK_ELEMENT = "network";
    public static final String SUBSCRIPTION_TIMEOUT_MINUTES = "subscription-timeout-minutes";
    public static final String SESSION_TIMEOUT = "session-timeout"; // Deprecated - renamed to subscription-timeout-minutes; retained for legacy config support.
    public static final int DEFAULT_TIMEOUT = 0; // Defaults to being invalidated when the associated FlexSession shuts down.

    /**
     * Constructs a default <code>NetworkSettings</code> instance with
     * default session timeout.
     */
    public NetworkSettings()
    {
        throttleSettings = new ThrottleSettings();
        subscriptionTimeoutMinutes = DEFAULT_TIMEOUT;
    }

    /**
     * Returns the cluster id.
     *
     * @return The cluster id.
     */
    public String getClusterId()
    {
        return clusterId;
    }

    /**
     * Sets the cluster id.
     *
     * @param id The cluster id.
     */
    public void setClusterId(String id)
    {
        this.clusterId = id;
    }

    /**
     * Returns the <code>subscription-timeout-minutes</code> property.
     *
     * @return the <code>subscription-timeout-minutes</code> property.
     */
    public int getSubscriptionTimeoutMinutes()
    {
        return subscriptionTimeoutMinutes;
    }

    /**
     * Sets the <code>subscription-timeout-minutes</code> property which is the idle time in
     * minutes before a subscriber that no messages are being pushed to will be unsubscribed.
     * A value of 0 will cause subscriptions to be kept alive as long as the associated FlexSession
     * they are created over is active.
     *
     * @param value The value to set the <code>subscription-timeout-minutes</code> property to.
     */
    public void setSubscriptionTimeoutMinutes(int value)
    {
        subscriptionTimeoutMinutes = value;
    }

    /**
     * Returns the <code>shared-backend</code> property.
     *
     * @return the <code>shared-backend</code> property.
     */
    public boolean isSharedBackend()
    {
        return sharedBackend;
    }

    /**
     * Sets the <code>shared-backend</code> property.
     *
     * @param sharedBackend The value to set the <code>shared-backend</code> property to.
     */
    public void setSharedBackend(boolean sharedBackend)
    {
        this.sharedBackend = sharedBackend;
        this.sharedBackendSet = true;
    }

    /**
     * Returns true if the shared backend has been set or false if it is
     * still using the default.
     *
     * @return <code>true</code> if the shared backend has been set; <code>false</code> otherwise.
     */
    public boolean isSharedBackendSet()
    {
        return sharedBackendSet;
    }

    /**
     * Returns the <code>ThrottleSettings</code> property.
     *
     * @return the <code>ThrottleSettings</code> property.
     */
    public ThrottleSettings getThrottleSettings()
    {
        return throttleSettings;
    }

    /**
     * Sets the <code>ThrottleSettings</code> property.
     *
     * @param value The value to set the <code>ThrottleSettings</code> property to.
     */
    public void setThrottleSettings(ThrottleSettings value)
    {
        throttleSettings = value;
    }

}
