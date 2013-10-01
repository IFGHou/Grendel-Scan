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
package flex.messaging;

import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import flex.messaging.client.FlexClient;
import flex.messaging.io.TypeMarshallingContext;

/**
 * The <tt>FlexContext</tt> is a utility class that exposes the current execution context. It provides access to <tt>FlexSession</tt> and <tt>FlexClient</tt> instances associated with the current
 * message being processed, as well as global context via the <tt>MessageBroker</tt>, <tt>ServletContext</tt> and <tt>ServletConfig</tt> for the application. Any, or all, of the properties exposed by
 * this class may be <code>null</code> depending upon the current execution context so test for that before attempting to interact with them.
 */
public class FlexContext
{
    private static ThreadLocal flexClients = new ThreadLocal();
    private static ThreadLocal sessions = new ThreadLocal();
    private static ThreadLocal messageBrokers = new ThreadLocal();
    private static ThreadLocal responses = new ThreadLocal();
    private static ThreadLocal requests = new ThreadLocal();
    private static ThreadLocal tunnelRequests = new ThreadLocal();
    private static ThreadLocal servletConfigs = new ThreadLocal();
    private static ThreadLocal messageFromPeer = new ThreadLocal();
    private static ThreadLocal messageRoutedNotifiers = new ThreadLocal();
    private static ServletConfig lastGoodServletConfig;

    private FlexContext()
    {
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalObjects(FlexClient flexClient, FlexSession session, MessageBroker broker, HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig)
    {
        flexClients.set(flexClient);
        sessions.set(session);
        messageBrokers.set(broker);
        requests.set(request);
        responses.set(response);
        servletConfigs.set(servletConfig);
        messageFromPeer.set(Boolean.FALSE);
        if (servletConfig != null)
            lastGoodServletConfig = servletConfig;
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalObjects(FlexClient flexClient, FlexSession session, MessageBroker broker)
    {
        setThreadLocalObjects(flexClient, session, broker, null, null, null);
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void clearThreadLocalObjects()
    {
        // first clear thread locals on the broker
        MessageBroker mb = (MessageBroker) messageBrokers.get();
        if (mb != null)
            mb.clearSystemSettingsThreadLocal();

        sessions.remove();
        messageBrokers.remove();
        requests.remove();
        responses.remove();
        tunnelRequests.remove();
        servletConfigs.remove();
        messageFromPeer.remove();

        TypeMarshallingContext.clearThreadLocalObjects();
    }

    /**
     * The HttpServletResponse for the current request if the request is via HTTP. Returns null if the client is using a non-HTTP channel. Available for users.
     */
    public static HttpServletRequest getHttpRequest()
    {
        return (HttpServletRequest) requests.get();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Users should not call this.
    // * @exclude
    // */
    // public static void setThreadLocalHttpRequest(HttpServletRequest value)
    // {
    // if (value == null)
    // requests.remove();
    // else
    // requests.set(value);
    // }

    /**
     * The HttpServletResponse for the current request if the request is via HTTP. Returns null if the using an non-HTTP channel. Available for users.
     */
    public static HttpServletResponse getHttpResponse()
    {
        return (HttpServletResponse) responses.get();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Users should not call this.
    // * @exclude
    // */
    // public static void setThreadLocalHttpResponse(HttpServletResponse value)
    // {
    // if (value == null)
    // responses.remove();
    // else
    // responses.set(value);
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * The HttpServletRequest for the current request if it is transporting a tunneled protocol.
    // * Returns null if the current request protocol it not tunneled.
    // * Available for users.
    // */
    // public static HttpServletRequest getTunnelHttpRequest()
    // {
    // return (HttpServletRequest)tunnelRequests.get();
    // }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalTunnelHttpRequest(HttpServletRequest value)
    {
        if (value == null)
            tunnelRequests.remove();
        else
            tunnelRequests.set(value);
    }

    /**
     * The ServletConfig for the current request, uses the last known ServletConfig when the request is not via HTTP. Available for users.
     */
    public static ServletConfig getServletConfig()
    {
        if (servletConfigs.get() != null)
        {
            return (ServletConfig) servletConfigs.get();
        }
        return lastGoodServletConfig;
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalServletConfig(ServletConfig value)
    {
        if (value == null)
            servletConfigs.remove();
        else
            servletConfigs.set(value);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * The ServletContext for the current web application.
    // */
    // public static ServletContext getServletContext()
    // {
    // return getServletConfig().getServletContext();
    // }

    /**
     * The FlexClient for the current request. Available for users.
     */
    public static FlexClient getFlexClient()
    {
        return (FlexClient) flexClients.get();
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalFlexClient(FlexClient flexClient)
    {
        if (flexClient == null)
            flexClients.remove();
        else
            flexClients.set(flexClient);
    }

    /**
     * The FlexSession for the current request. Available for users.
     */
    public static FlexSession getFlexSession()
    {
        return (FlexSession) sessions.get();
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setThreadLocalSession(FlexSession session)
    {
        if (session == null)
            sessions.remove();
        else
            sessions.set(session);
    }

    /**
     * The MessageBroker for the current request. Not available for users.
     * 
     * @exclude
     */
    public static MessageBroker getMessageBroker()
    {
        return (MessageBroker) messageBrokers.get();
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Users should not call this.
    // * @exclude
    // */
    // public static void setThreadLocalMessageBroker(MessageBroker value)
    // {
    // if (value == null)
    // messageBrokers.remove();
    // else
    // messageBrokers.set(value);
    // }

    // TODO UCdetector: Remove unused code:
    // /**
    // * Users should not call this.
    // * @exclude
    // */
    // public static MessageRoutedNotifier getMessageRoutedNotifier()
    // {
    // return (MessageRoutedNotifier)messageRoutedNotifiers.get();
    // }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static void setMessageRoutedNotifier(MessageRoutedNotifier value)
    {
        if (value == null)
            messageRoutedNotifiers.remove();
        else
            messageRoutedNotifiers.set(value);
    }

    /**
     * Indicates whether the current message being processed came from a server peer in a cluster.
     */
    public static boolean isMessageFromPeer()
    {
        return (Boolean) messageFromPeer.get();
    }

    /**
     * Sets a thread local indicating whether the message being processed came from a server peer in a cluster.
     * 
     * @param value
     *            True if the message came from a peer; otherwise false.
     * 
     * @exclude
     */
    public static void setMessageFromPeer(boolean value)
    {
        messageFromPeer.set(value);
    }

    /**
     * Users should not call this.
     * 
     * @exclude
     */
    public static boolean isPerClientAuthentication()
    {
        if (getMessageBroker().getLoginManager() == null)
        {
            return false;
        }
        else
        {
            return getMessageBroker().getLoginManager().isPerClientAuthentication();
        }
    }

    /**
     * Returns the principal associated with the session or client depending on whether perClientauthentication is being used. If the client has not authenticated the principal will be null.
     * 
     * @return The principal associated with the session.
     */
    public static Principal getUserPrincipal()
    {
        if (isPerClientAuthentication())
        {
            FlexClient client = getFlexClient();
            if (client != null)
                return client.getUserPrincipal();
        }
        else
        {
            FlexSession session = getFlexSession();
            if (session != null)
                return session.getUserPrincipal();
        }
        return null;
    }

    /**
     * Sets the Principal on either the current FlexClient or FlexSession depending upon whether perClientAuthentication is in use.
     * 
     * @param userPrincipal
     *            The principal to associate with the FlexClient or FlexSession depending upon whether perClientAuthentication is in use.
     */
    public static void setUserPrincipal(Principal userPrincipal)
    {
        if (isPerClientAuthentication())
            getFlexClient().setUserPrincipal(userPrincipal);
        else
            getFlexSession().setUserPrincipal(userPrincipal);
    }

    // TODO UCdetector: Remove unused code:
    // /**
    // * @exclude
    // * Create the static thread local storage.
    // */
    // public static void createThreadLocalObjects()
    // {
    // // allocate if needed
    // if (flexClients == null)
    // {
    // flexClients = new ThreadLocal();
    // sessions = new ThreadLocal();
    // messageBrokers = new ThreadLocal();
    // responses = new ThreadLocal();
    // requests = new ThreadLocal();
    // tunnelRequests = new ThreadLocal();
    // servletConfigs = new ThreadLocal();
    // messageFromPeer = new ThreadLocal();
    // messageRoutedNotifiers = new ThreadLocal();
    // }
    // }

    /**
     * @exclude Destroy the static thread local storage. Call ONLY on shutdown
     */
    public static void releaseThreadLocalObjects()
    {
        // ThreadLocals
        flexClients = null;
        sessions = null;
        messageBrokers = null;
        responses = null;
        requests = null;
        tunnelRequests = null;
        servletConfigs = null;
        messageFromPeer = null;
        messageRoutedNotifiers = null;
        // static
        lastGoodServletConfig = null;
    }
}
