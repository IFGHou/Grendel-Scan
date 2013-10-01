/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.config;

/**
 * This configuration class is derived from optional properties that may be supplied in the &lt;properties&gt; section of a destination. It exists to capture properties related to message thottling in
 * a way that simplifies the ThrottleManager's usage of the configuration.
 */
public class ThrottleSettings
{
    /** Integer value of no policy **/
    public static final int POLICY_NONE = 0;
    /** Integer value of error policy **/
    public static final int POLICY_ERROR = 1;
    /** Integer value of ignore policy **/
    public static final int POLICY_IGNORE = 2;
    /**
     * Integer value of replace policy. Note that replace policy is currently not used, and functionally the same as ignore policy.
     */
    public static final int POLICY_REPLACE = 3;

    /** @exclude **/
    public static final String POLICY_NONE_STRING = "NONE";
    /** @exclude **/
    public static final String POLICY_ERROR_STRING = "ERROR";
    /** @exclude **/
    public static final String POLICY_IGNORE_STRING = "IGNORE";
    /** @exclude **/
    public static final String POLICY_REPLACE_STRING = "REPLACE";

    /** @exclude **/
    public static final String ELEMENT_INBOUND = "throttle-inbound";
    /** @exclude **/
    public static final String ELEMENT_OUTBOUND = "throttle-outbound";
    /** @exclude **/
    public static final String ELEMENT_POLICY = "policy";
    /** @exclude **/
    public static final String ELEMENT_DEST_FREQ = "max-frequency";
    /** @exclude **/
    public static final String ELEMENT_CLIENT_FREQ = "max-client-frequency";

    private String destinationName;
    private int inClientMessagesPerSec;
    private int inDestinationMessagesPerSec;
    private int outClientMessagesPerSec;
    private int outDestinationMessagesPerSec;
    private int inPolicy;
    private int outPolicy;

    /**
     * Creates a <code>ThrottleSettings</code> instance with default settings.
     */
    public ThrottleSettings()
    {
        inPolicy = POLICY_NONE;
        outPolicy = POLICY_NONE;
        inClientMessagesPerSec = 0;
        inDestinationMessagesPerSec = 0;
        outDestinationMessagesPerSec = 0;
    }

    /**
     * Parses the throttle policy out of the given string.
     * 
     * @param policy
     *            The string policy to parse.
     */
    public static int parsePolicy(String policy)
    {
        if (POLICY_NONE_STRING.equalsIgnoreCase(policy))
            return POLICY_NONE;
        else if (POLICY_IGNORE_STRING.equalsIgnoreCase(policy))
            return POLICY_IGNORE;
        else if (POLICY_ERROR_STRING.equalsIgnoreCase(policy))
            return POLICY_ERROR;
        else if (POLICY_REPLACE_STRING.equalsIgnoreCase(policy))
            return POLICY_REPLACE;

        ConfigurationException ex = new ConfigurationException();
        ex.setMessage("Unsupported throttle policy '" + policy + "'");
        throw ex;
    }

    /**
     * Returns whether client throttling is enabled.
     * 
     * @return <code>true</code> if the incoming client frequency or outgoing client frequency is greater than zero; otherwise <code>false</code>.
     */
    public boolean isClientThrottleEnabled()
    {
        if (getIncomingClientFrequency() > 0 || getOutgoingClientFrequency() > 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns whether destination throttling is enabled.
     * 
     * @return <code>true</code> if incoming destination frequency or outgoing destination frequency is greater than zero; otherwise <code>false</code>.
     */
    public boolean isDestinationThrottleEnabled()
    {
        if (getIncomingDestinationFrequency() > 0 || getOutgoingDestinationFrequency() > 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns the inbound throttle policy.
     * 
     * @return the inbound throttle policy.
     */
    public int getInboundPolicy()
    {
        return inPolicy;
    }

    /**
     * Sets inbound throttle policy. The inbound policy may be ERROR or IGNORE.
     * 
     * @param inPolicy
     *            The inbound policy.
     */
    public void setInboundPolicy(int inPolicy)
    {
        if (inPolicy == POLICY_REPLACE)
        {
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage("The REPLACE throttle policy applies to outbound throttling only");
            throw ex;
        }
        this.inPolicy = inPolicy;
    }

    /**
     * Returns the outbound throttle policy.
     * 
     * @return the outbound throttle policy.
     */
    public int getOutboundPolicy()
    {
        return outPolicy;
    }

    /**
     * Sets the outbound throttle policy. The outbound policy may be ERROR, IGNORE, or REPLACE. Note that replace policy is currently not used, and functionally the same as ignore policy.
     * 
     * @param outPolicy
     *            The outbound policy.
     */
    public void setOutboundPolicy(int outPolicy)
    {
        this.outPolicy = outPolicy;
    }

    /**
     * Returns the destination name for <code>ThrottleSettings</code>.
     * 
     * @return the destination name for <code>ThrottleSettings</code>.
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * Sets the destination name for <code>ThrottleSettings</code>. This is set automatically when <code>NetworkSettings</code> is assigned to a destination.
     * 
     * @param destinationName
     *            The destination name.
     */
    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * Returns the incoming client frequency (max-client-frequency).
     * 
     * @return The incoming client frequency (max-client-frequency).
     */
    public int getIncomingClientFrequency()
    {
        return inClientMessagesPerSec;
    }

    /**
     * Sets the incoming client frequency (max-client-frequency). Optional and the default value is 0. Note that the incoming client frequency cannot be more than the incoming destination frequency.
     * 
     * @param n
     *            The incoming client frequency.
     */
    public void setIncomingClientFrequency(int n)
    {
        if (inDestinationMessagesPerSec > 0 && n > inDestinationMessagesPerSec)
        {
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage("The incoming client frequency '" + n + "' cannot be more than the incoming destination frequency '" + inDestinationMessagesPerSec + "'");
            throw ex;
        }
        this.inClientMessagesPerSec = n;
    }

    /**
     * Returns the incoming destination frequency (max-frequency).
     * 
     * @return The incoming destination frequency (max-frequency).
     */
    public int getIncomingDestinationFrequency()
    {
        return inDestinationMessagesPerSec;
    }

    /**
     * Sets the incoming destination frequency (max-frequency). Optional and the default value is 0. Note that the incoming destination frequency cannot be less than the incoming client frequency.
     * 
     * @param n
     *            The incoming destination frequency.
     */
    public void setIncomingDestinationFrequency(int n)
    {
        if (n < inClientMessagesPerSec)
        {
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage("The incoming destination frequency '" + n + "' cannot be less than the incoming client frequency '" + inClientMessagesPerSec + "'");
            throw ex;
        }
        this.inDestinationMessagesPerSec = n;
    }

    /**
     * Returns the outgoing client frequency (max-client-frequency).
     * 
     * @return The outgoing client frequency (max-client-frequency).
     */
    public int getOutgoingClientFrequency()
    {
        return outClientMessagesPerSec;
    }

    /**
     * Sets the outgoing client frequency (max-client-frequency). Optional and the default value is 0. Note that the outgoing client frequency cannot be more than the outgoing destination frequency.
     * 
     * @param n
     *            The outgoing client frequency.
     */
    public void setOutgoingClientFrequency(int n)
    {
        if (outDestinationMessagesPerSec > 0 && n > outDestinationMessagesPerSec)
        {
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage("The outgoing client frequency '" + n + "' cannot be more than the outgoing destination frequency '" + outDestinationMessagesPerSec + "'");
            throw ex;
        }
        this.outClientMessagesPerSec = n;
    }

    /**
     * Returns the outgoing destination frequency (max-frequency).
     * 
     * @return The outgoing destination frequency (max-frequency).
     */
    public int getOutgoingDestinationFrequency()
    {
        return outDestinationMessagesPerSec;
    }

    /**
     * Sets the outgoing destination frequency (max-frequency). Optional and the default value is 0. Note that the outgoing destination frequency cannot be less than the outgoing client frequency.
     * 
     * @param n
     *            The outgoing destination frequency.
     */
    public void setOutgoingDestinationFrequency(int n)
    {
        if (n < outClientMessagesPerSec)
        {
            ConfigurationException ex = new ConfigurationException();
            ex.setMessage("The outgoing destination frequency '" + n + "' cannot be less than the outgoing client frequency '" + outClientMessagesPerSec + "'");
            throw ex;
        }
        this.outDestinationMessagesPerSec = n;
    }
}
