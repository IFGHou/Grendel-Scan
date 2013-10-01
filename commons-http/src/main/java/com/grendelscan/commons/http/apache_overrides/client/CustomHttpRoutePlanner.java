package com.grendelscan.commons.http.apache_overrides.client;

import java.net.InetAddress;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.protocol.HttpContext;

/**
 * Based on DefaultHttpRoutePlanner from Apache
 */
public class CustomHttpRoutePlanner implements HttpRoutePlanner
{
    private ClientConnectionManager connectionManager;

    public CustomHttpRoutePlanner(final ClientConnectionManager aConnManager)
    {
        setConnectionManager(aConnManager);
    }

    // default constructor

    // non-javadoc, see interface HttpRoutePlanner
    @Override
    public HttpRoute determineRoute(final HttpHost target, final HttpRequest request, final HttpContext context)
    {

        if (request == null)
        {
            throw new IllegalStateException("Request must not be null.");
        }

        // If we have a forced route, we can do without a target.
        HttpRoute route = (HttpRoute) request.getParams().getParameter(ConnRoutePNames.FORCED_ROUTE);
        if (route != null)
        {
            return route;
        }

        // If we get here, there is no forced route.
        // So we need a target to compute a route.

        if (target == null)
        {
            throw new IllegalStateException("Target host must not be null.");
        }

        final InetAddress local = (InetAddress) request.getParams().getParameter(ConnRoutePNames.LOCAL_ADDRESS);
        final HttpHost proxy = (HttpHost) request.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);

        final Scheme schm = connectionManager.getSchemeRegistry().getScheme(target.getSchemeName());
        // as it is typically used for TLS/SSL, we assume that
        // a layered scheme implies a secure connection
        final boolean secure = schm.isLayered();

        if (proxy == null)
        {
            route = new HttpRoute(target, local, secure);
        }
        else
        {
            route = new HttpRoute(target, local, proxy, secure);
        }

        // Map<HttpRoute, Integer> maxConnectionsPerHost = (Map<HttpRoute, Integer>) request.getParams().getParameter(ConnManagerPNames.MAX_HOST_CONNECTIONS);
        // if (maxConnectionsPerHost == null)
        // {
        // maxConnectionsPerHost = new HashMap<HttpRoute, Integer>(1);
        // request.getParams().setParameter(ConnManagerPNames.MAX_HOST_CONNECTIONS, maxConnectionsPerHost);
        // }
        // if (!maxConnectionsPerHost.containsKey(route))
        // {
        // // maxConnectionsPerHost.put(route, scan.getScanSettings().getMaxConnectionsPerServer());
        // maxConnectionsPerHost.put(route, 100);
        // }

        return route;
    }

    public void setConnectionManager(final ClientConnectionManager aConnManager)
    {
        connectionManager = aConnManager;
    }

}
