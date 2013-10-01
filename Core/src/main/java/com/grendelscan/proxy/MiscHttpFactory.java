package com.grendelscan.proxy;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
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
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;

import com.grendelscan.GrendelScan;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.abstractProxy.AbstractProxyRequestHandler;
import com.grendelscan.requester.http.SSL.CustomSSLSocketFactory;


public class MiscHttpFactory
{
	public static HttpParams createDefaultHttpProxyParams()
	{
		Log.trace("MiscHttpFactory.createDefaultHttpProxyParams");
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024);
		params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
		params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
		params.setParameter(CoreProtocolPNames.ORIGIN_SERVER, GrendelScan.versionHttpText); 

//		ConnPerRouteBean maxConnectionsPerRoute = new ConnPerRouteBean();
//		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, maxConnectionsPerRoute);
//		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 5);
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 500);
		params.setIntParameter(CoreConnectionPNames.SO_LINGER, 10000);
		params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		
		return params;
	}
	
	public static ClientConnectionManager createProxyClientConnectionManager()
	{
		Log.trace("MiscHttpFactory.createProxyClientConnectionManager");
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
	        	Log.fatal("Problem creating SSLSocketFactory: " + e.toString(), e);
	        	System.exit(0);
			}
			catch (NoSuchAlgorithmException e)
			{
	        	Log.fatal("Problem creating SSLSocketFactory: " + e.toString(), e);
	        	System.exit(0);
			}
		
		
		return new ThreadSafeClientConnManager(supportedSchemes);
	}

    public static HttpContext createHttpProxyContext()
	{
		Log.trace("MiscHttpFactory.createHttpProxyContext");
		HttpContext context = new BasicHttpContext(null);
		return context;
	}

	public static HttpService createHttpServiceProxy(AbstractProxyRequestHandler requestHandler)
	{
		Log.trace("MiscHttpFactory.createHttpServiceProxy");
		HttpParams proxyParams = createDefaultHttpProxyParams();
		// Set up the HTTP protocol processor
		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		httpproc.addInterceptor(new ResponseConnControl());
		
		// Set up request handlers
		HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
		registry.register("*", requestHandler);
		
		// Set up the HTTP service  
		HttpService httpService =
		        new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), registry, proxyParams);
		
		return httpService;
	}
}
