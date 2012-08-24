package com.grendelscan.requester.http.apache_overrides.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.RFC2617Scheme;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.SSL.CustomSSLSocketFactory;
import com.grendelscan.scan.Scan;

public class ClientUtilities
{
	public static ClientConnectionManager createClientConnectionManager(HttpParams params)
	{
		SchemeRegistry supportedSchemes;
		
		supportedSchemes = new SchemeRegistry();
		
		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		
		CustomSSLSocketFactory sslsf;
        try
		{
			sslsf = new CustomSSLSocketFactory();
			supportedSchemes.register(new Scheme("https", sslsf, 443));
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
		
		return new ThreadSafeClientConnManager(params, supportedSchemes);
	}
	
	public static HttpParams createHttpParams()
	{
		return createHttpParams(Scan.getScanSettings().getMaxTotalConnections(), Scan.getScanSettings().getMaxConnectionsPerServer());
	}
	
	public static HttpParams createHttpParams(int maxTotalConnections, int maxConnectionsPerServer)
	{

		// prepare parameters
		HttpParams params = new BasicHttpParams();

		ConnPerRouteBean maxConnectionsPerRoute = new ConnPerRouteBean();
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, maxConnectionsPerRoute);
		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, maxTotalConnections);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		params.setParameter(CoreProtocolPNames.USER_AGENT, Scan.getScanSettings().getUserAgentString());
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, Scan.getScanSettings().getSocketReadTimeout() * 1000);
		params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		if (Scan.getScanSettings().getUseUpstreamProxy())
		{
			HttpHost proxy = new HttpHost(Scan.getScanSettings().getUpstreamProxyAddress(), Scan.getScanSettings().getUpstreamProxyPort());
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return params;
	}
	
	@SuppressWarnings("incomplete-switch")
    public static HttpContext createHttpContext()
	{
		HttpContext context = new BasicHttpContext(null);
		if (Scan.getScanSettings().getUseUpstreamProxy())
		{
			if (!Scan.getScanSettings().getUpstreamProxyUsername().equals(""))
			{
				AuthState authState = new AuthState();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(Scan.getScanSettings().getUpstreamProxyUsername(), Scan.getScanSettings().getUpstreamProxyPassword());
				authState.setCredentials(credentials);
				RFC2617Scheme authScheme = null;
				switch (Scan.getScanSettings().getUpstreamProxyAuthenticationType())
				{
					case BASIC: authScheme = new BasicScheme(); break;
					case DIGEST: authScheme = new DigestScheme(); break;
				}
				authState.setAuthScheme(authScheme);
				context.setAttribute(ClientContext.PROXY_AUTH_STATE, authState);
			}
		}
		return context;
	}
}