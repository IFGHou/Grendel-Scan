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
import flex.messaging.util.StringUtils;

/**
 * The channel configuration is intentionally generic so that
 * other channels can be added in the future. This format also allows
 * server-endpoint specific and client code-generation specific settings
 * to be modified without affecting needing to update the configuration
 * parser.
 *
 * @author Peter Farland
 * @exclude
 */
public class ChannelSettings extends PropertiesSettings
{
    protected String id;
    protected boolean remote;
    protected String serverId;
    private String sourceFile;

    protected SecurityConstraint constraint;

    // ENDPOINT
    protected String uri;
    protected int port;
    protected String endpointType;
    protected String clientType;

    protected String parsedUri;
    protected boolean contextParsed;
    protected String parsedClientUri;
    protected boolean clientContextParsed;

/* TODO UCdetector: Remove unused code: 
    public ChannelSettings(String id)
    {
        this.id = id;
    }
*/

    public String getId()
    {
        return id;
    }

    public boolean isRemote()
    {
        return remote;
    }

    public void setRemote(boolean value)
    {
        remote = value;
    }

    public String getServerId()
    {
        return serverId;
    }

    public void setServerId(String value)
    {
        serverId = value;
    }

    public String getClientType()
    {
        return clientType;
    }

    public void setClientType(String type)
    {
        this.clientType = type;
    }


    String getSourceFile()
    {
        return sourceFile;
    }

    void setSourceFile(String sourceFile)
    {
        this.sourceFile = sourceFile;
    }

    /**
     * A return value of 0 denotes no port in channel url.
     *
     * @return the port number for this channel
     * or 0 if channel url does not contain a port number
     */
    public int getPort()
    {
        return port;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
        port = parsePort(this, uri);
        contextParsed = false;
        clientContextParsed = false;
    }

    public String getClientParsedUri(String contextPath)
    {
        if (!clientContextParsed)
            parseClientUri(this, contextPath);

        return parsedClientUri;
    }

    public String getEndpointType()
    {
        return endpointType;
    }

    public void setEndpointType(String type)
    {
        this.endpointType = type;
    }

    public SecurityConstraint getConstraint()
    {
        return constraint;
    }

    public void setConstraint(SecurityConstraint constraint)
    {
        this.constraint = constraint;
    }

    /**
     * In this client version of the URI parser we're just looking to
     * replace the context root tokens.
     */
    private static void parseClientUri(ChannelSettings cs, String contextPath)
    {
        if (!cs.clientContextParsed)
        {
            String channelEndpoint = cs.getUri().trim();

            // either {context-root} or {context.root} is legal
            channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", ConfigurationConstants.CONTEXT_PATH_TOKEN);

            if ((contextPath == null) && (channelEndpoint.indexOf(ConfigurationConstants.CONTEXT_PATH_TOKEN) != -1))
            {
                // context root must be specified before it is used
                ConfigurationException e = new ConfigurationException();
                e.setMessage(ConfigurationConstants.UNDEFINED_CONTEXT_ROOT, new Object[]{cs.getId()});
                throw e;
            }

            // simplify the number of combinations to test by ensuring our
            // context path always starts with a slash
            if (contextPath != null && !contextPath.startsWith("/"))
            {
                contextPath = "/" + contextPath;
            }

            // avoid double-slashes from context root by replacing /{context.root}
            // in a single replacement step
            if (channelEndpoint.indexOf(ConfigurationConstants.SLASH_CONTEXT_PATH_TOKEN) != -1)
            {
                // but avoid double-slash for /{context.root}/etc when we have
                // the default context root
                if ("/".equals(contextPath) && !ConfigurationConstants.SLASH_CONTEXT_PATH_TOKEN.equals(channelEndpoint))
                    contextPath = "";

                channelEndpoint = StringUtils.substitute(channelEndpoint, ConfigurationConstants.SLASH_CONTEXT_PATH_TOKEN, contextPath);
            }
            // otherwise we have something like {server.name}:{server.port}{context.root}...
            else
            {
                // but avoid double-slash for {context.root}/etc when we have
                // the default context root
                if ("/".equals(contextPath) && !ConfigurationConstants.CONTEXT_PATH_TOKEN.equals(channelEndpoint))
                    contextPath = "";

                channelEndpoint = StringUtils.substitute(channelEndpoint, ConfigurationConstants.CONTEXT_PATH_TOKEN, contextPath);
            }

            cs.parsedClientUri = channelEndpoint;
            cs.clientContextParsed = true;
        }
    }

    /**
     * Returns the port number specified in the url or 0 if the URL
     * does not contain a port number.
     *
     * @param cs Channel Settings
     * @param url The url to parse for contained port number
     * @return the port number specific in the url or 0 if the url
     * does not contain a port number
     */
    private static int parsePort(ChannelSettings cs, String url)
    {
        int port = 0;

        // rtmp://localhost:2035/foo/bar
        // Find first slash with colon
        int start = url.indexOf(":/");
        if (start > 0)
        {
            // second slash should be +1, so start 3 after for ://
            start = start + 3;
            int end = url.indexOf('/', start);

            // take everything up until the next slash for servername:port
            String snp = end == -1 ? url.substring(start) : url.substring(start, end);

            // If IPv6 is in use, start looking after the square bracket.
            int delim = snp.indexOf("]");
            delim = (delim > -1)? snp.indexOf(":", delim) : snp.indexOf(":");

            if (delim > 0)
            {
                try
                {
                    int p = Integer.parseInt(snp.substring(delim + 1));
                    if (p > 0)
                        port = p;
                }
                catch (Throwable t)
                {
                }
            }
            // If a colon doesn't exist here, then there is no specified port.
            // Such channels are supported and 0 will be returned to denote this.
        }
        return port;
    }

    /**
     * Remove protocol, host, port and context-root from a given url.
     * Unlike parseClientUri, this method does not check if the channel
     * setting has been parsed before.
     *
     * @param url Original url.
     * @return Url with protocol, host, port and context-root removed.
     */
    public static String removeTokens(String url)
    {
        String channelEndpoint = url.toLowerCase().trim();

        // remove protocol and host info
        if (channelEndpoint.startsWith("http://") ||
                channelEndpoint.startsWith("https://") ||
                channelEndpoint.startsWith("rtmp://") ||
                channelEndpoint.startsWith("rtmps://")) {
            int nextSlash = channelEndpoint.indexOf('/', 8);
            // Check to see if there is a 'next slash', and also that the next
            // slash isn't the last character
            if ((nextSlash > 0) && (nextSlash != channelEndpoint.length()-1))
                channelEndpoint = channelEndpoint.substring(nextSlash);
        }

        // either {context-root} or {context.root} is legal
        channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", ConfigurationConstants.CONTEXT_PATH_TOKEN);

        // Remove context path info
        if (channelEndpoint.startsWith(ConfigurationConstants.CONTEXT_PATH_TOKEN))
        {
            channelEndpoint = channelEndpoint.substring(ConfigurationConstants.CONTEXT_PATH_TOKEN.length());
        }
        else if (channelEndpoint.startsWith(ConfigurationConstants.SLASH_CONTEXT_PATH_TOKEN))
        {
            channelEndpoint = channelEndpoint.substring(ConfigurationConstants.SLASH_CONTEXT_PATH_TOKEN.length());
        }

        // We also don't match on trailing slashes
        if (channelEndpoint.endsWith("/"))
        {
            channelEndpoint = channelEndpoint.substring(0, channelEndpoint.length() - 1);
        }
        return channelEndpoint;
    }

}
