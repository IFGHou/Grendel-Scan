package com.grendelscan.proxy;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.ssl.CustomSSLSocketFactory;

public class MiscHttpFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscHttpFactory.class);

    public static HttpParams createDefaultHttpProxyParams()
    {
        LOGGER.trace("MiscHttpFactory.createDefaultHttpProxyParams");
        HttpParams params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024);
        params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
        params.setParameter(CoreProtocolPNames.ORIGIN_SERVER, GrendelScan.versionHttpText);

        // ConnPerRouteBean maxConnectionsPerRoute = new ConnPerRouteBean();
        // params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, maxConnectionsPerRoute);
        // params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 5);
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 500);
        params.setIntParameter(CoreConnectionPNames.SO_LINGER, 10000);
        params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

        return params;
    }

    public static HttpContext createHttpProxyContext()
    {
        LOGGER.trace("MiscHttpFactory.createHttpProxyContext");
        HttpContext context = new BasicHttpContext(null);
        return context;
    }

    public static HttpService createHttpServiceProxy(final AbstractProxyRequestHandler requestHandler)
    {
        LOGGER.trace("MiscHttpFactory.createHttpServiceProxy");
        HttpParams proxyParams = createDefaultHttpProxyParams();
        // Set up the HTTP protocol processor
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new ResponseConnControl());

        // Set up request handlers
        HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
        registry.register("*", requestHandler);

        // Set up the HTTP service
        HttpService httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), registry, proxyParams);

        return httpService;
    }

    public static ClientConnectionManager createProxyClientConnectionManager()
    {
        LOGGER.trace("MiscHttpFactory.createProxyClientConnectionManager");
        SchemeRegistry supportedSchemes;

        supportedSchemes = new SchemeRegistry();

        // Register the "http" and "https" protocol schemes, they are
        // required by the default operator to look up socket factories.
        SchemeSocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", 80, sf));

        CustomSSLSocketFactory sslsf;
        try
        {
            sslsf = new CustomSSLSocketFactory();
            supportedSchemes.register(new Scheme("https", 443, sslsf));
        }
        catch (KeyManagementException e)
        {
            LOGGER.error("Problem creating SSLSocketFactory: " + e.toString(), e);
            System.exit(0);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("Problem creating SSLSocketFactory: " + e.toString(), e);
            System.exit(0);
        }

        return new ThreadSafeClientConnManager(supportedSchemes);
    }
}
