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
package flex.messaging.client;

/**
 * A class to hold user agent specific properties. For example, in streaming endpoints, a certain number of bytes need to be written before the streaming connection can be used and this value is
 * specific to user agents. Similarly, the number of simultaneous connections a session can have is user agent specific.
 */
public class UserAgentSettings
{
    /**
     * The prefix of the version token used by IE in its user agent values. This lets us sniff for all IE versions.
     */
    public static final String GENERIC_MSIE_USER_AGENT = "MSIE";

    /**
     * Bytes needed to kickstart the streaming connections for IE.
     */
    public static final int MSIE_KICKSTART_BYTES = 2048;

    /**
     * The prefix of the version token used by Firefox in its user agent values. This lets us sniff for all Firefox versions.
     */
    public static final String GENERIC_FIREFOX_USER_AGENT = "Firefox";

    /**
     * Bytes needed to kickstart the streaming connections for Firefox.
     */
    public static final int FIREFOX_KICKSTART_BYTES = 0;

    /**
     * The default number of streaming connections per session. In IE, there can be 4 persistent HTTP connections per session. In Firefox, this number is determined by
     * network.http.max-connections-per-server config which is 8 by default. Streaming endpoints can technically support one less than this number of streaming connections per session (3 for IE, 7 for
     * Firefox) but by default, we will have one streaming connection per session which can be changed by increasing maxStreamingConnectionsPerSession property if needed.
     */
    public static final int DEFAULT_MAX_STREAMING_CONNECTIONS_PER_SESSION = 1;

    private String matchOn;
    private int kickstartBytes;
    private int maxStreamingConnectionsPerSession = DEFAULT_MAX_STREAMING_CONNECTIONS_PER_SESSION;

    /**
     * Static method to retrieve pre-initialized IE and Firefox user agents.
     * 
     * @param matchOn
     *            String to use match the agent.
     */
    public static UserAgentSettings getAgent(String matchOn)
    {
        UserAgentSettings userAgent = new UserAgentSettings();
        userAgent.setMatchOn(matchOn);
        userAgent.setMaxStreamingConnectionsPerSession(DEFAULT_MAX_STREAMING_CONNECTIONS_PER_SESSION);
        if (GENERIC_MSIE_USER_AGENT.equals(matchOn))
            userAgent.setKickstartBytes(MSIE_KICKSTART_BYTES);
        else if (GENERIC_FIREFOX_USER_AGENT.equals(matchOn))
            userAgent.setKickstartBytes(FIREFOX_KICKSTART_BYTES);
        return userAgent;
    }

    /**
     * Returns the String to use to match the agent.
     * 
     * @return The String to use to match the agent.
     */
    public String getMatchOn()
    {
        return matchOn;
    }

    /**
     * Sets the String to use to match the agent.
     * 
     * @param matchOn
     *            The String to use to match the agent.
     */
    public void setMatchOn(String matchOn)
    {
        this.matchOn = matchOn;
    }

    /**
     * Returns the number of bytes needed to kickstart the streaming connections for the user agent.
     * 
     * @return The number of bytes needed to kickstart the streaming connections for the user agent.
     */
    public int getKickstartBytes()
    {
        return kickstartBytes;
    }

    /**
     * Sets the number of bytes needed to kickstart the streaming connections for the user agent.
     * 
     * @param kickstartBytes
     *            The number of bytes needed to kickstart the streaming connections for the user agent.
     */
    public void setKickstartBytes(int kickstartBytes)
    {
        if (kickstartBytes < 0)
            kickstartBytes = 0;
        this.kickstartBytes = kickstartBytes;
    }

    /**
     * Returns the number of simultaneous streaming connections per session the user agent supports.
     * 
     * @return The number of streaming connections per session the user agent supports.
     */
    public int getMaxStreamingConnectionsPerSession()
    {
        return maxStreamingConnectionsPerSession;
    }

    /**
     * Sets the number of simultaneous streaming connections per session the user agent supports.
     * 
     * @param maxStreamingConnectionsPerSession
     *            The number of simultaneous streaming connections per session the user agent supports.
     */
    public void setMaxStreamingConnectionsPerSession(int maxStreamingConnectionsPerSession)
    {
        this.maxStreamingConnectionsPerSession = maxStreamingConnectionsPerSession;
    }
}
