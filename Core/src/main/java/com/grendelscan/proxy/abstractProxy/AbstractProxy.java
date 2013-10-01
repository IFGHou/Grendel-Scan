package com.grendelscan.proxy.abstractProxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.grendelscan.logging.Log;
import com.grendelscan.proxy.ProxyConfig;
import com.grendelscan.requester.http.apache_overrides.client.ClientUtilities;
import com.grendelscan.requester.http.apache_overrides.client.CustomHttpClient;
import com.grendelscan.scan.ConfigurationManager;
import com.grendelscan.scan.Scan;

public abstract class AbstractProxy
{
	protected final ProxyConfig config;
	protected ServerSocket serverSocket;
	protected boolean running;
	protected static int maxThreads;
	protected GenericProxyRequestListenerThread[] requestListenerThreads;
	protected final Object runningLock = new Object(); 

	protected static CustomHttpClient proxyHttpClient;
	protected static ClientConnectionManager ccm;
	private static int maxProxyQueueLength;
	private static boolean initialized = false;
	
	public AbstractProxy(ProxyConfig config)
	{
		this.config = config;
		Log.trace("Instantiating proxy on " +getProxyDescription());
	}
	
	protected void createSocket() throws IOException
	{
		Log.trace("Creating socket for proxy on " + getProxyDescription());
		try
		{
			if (config.getBindIP().equals("0.0.0.0"))
			{
				Log.trace("Starting socket bound to all IPs for proxy on " + getProxyDescription());
				serverSocket = new ServerSocket(config.getBindPort(), maxProxyQueueLength);
			}
			else
			{
				Log.trace("Starting socket bound to specific IP for proxy on " + getProxyDescription());
				InetAddress bindInetAddress = InetAddress.getByName(config.getBindIP());
				serverSocket = new ServerSocket(config.getBindPort(), maxProxyQueueLength, bindInetAddress);
			}
			serverSocket.setSoTimeout(500);
		}
		catch (IOException e) 
		{
			Log.error("Problem opening proxy socket: " + e.toString(), e);
			
			Scan.getInstance().displayMessage("Error:", "The " + getName() + " failed to start. You may need to change\n" +
						"some parameters. The error message is: \n\n" + e.getMessage());
			throw e;
		}

	}
	
	
	protected synchronized void initParams()
	{
		if (!initialized)
		{
			maxThreads = ConfigurationManager.getInt("proxy.thread_count", 5);
			HttpParams clientParams = ClientUtilities.createHttpParams(); 
			HttpContext clientContext = ClientUtilities.createHttpContext();
			
			ccm = ClientUtilities.createClientConnectionManager(10, 5); // need to put these in a config file
			proxyHttpClient = new CustomHttpClient(ccm, clientParams, clientContext);
	
			maxProxyQueueLength = ConfigurationManager.getInt("proxy.max_queue_length", 20);
			initialized = true;
		}
	}
	
//	private HttpParams createServerHttpParams()
//	{
//
//		// prepare parameters
//		HttpParams params = new BasicHttpParams();
//
//		ConnPerRouteBean maxConnectionsPerRoute = new ConnPerRouteBean();
//		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, maxConnectionsPerRoute);
//		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 5);
//		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//		HttpProtocolParams.setContentCharset(params, "UTF-8");
//		params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
//		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
//		params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
//		params.setParameter(CoreProtocolPNames.ORIGIN_SERVER, GrendelScan.versionHttpText);
//		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
//		params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
//		if (scanSettings.getUseUpstreamProxy())
//		{
//			HttpHost proxy = new HttpHost(scanSettings.getUpstreamProxyAddress(), scanSettings.getUpstreamProxyPort());
//			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//		}
//		return params;
//	}
	

	
//	private static void initSSL()
//	{
//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		SSLSocketFactory secureSocketFactory = null;
//		KeyStore trustStore;
//		try
//		{
//			trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//			secureSocketFactory = new SSLSocketFactory(trustStore);
//		}
//		catch (KeyStoreException e) 
//		{
//			Debug.errDebug("Problem initializing KeyStore: " + e.toString(), e);
//			System.exit(1);
//		}
//		catch (NoSuchAlgorithmException e)
//		{
//			Debug.errDebug("Problem initializing KeyStore: " + e.toString(), e);
//			System.exit(1);
//		}
//		catch (KeyManagementException e)
//		{
//			Debug.errDebug("Problem initializing KeyStore: " + e.toString(), e);
//			System.exit(1);
//		}
//		catch (UnrecoverableKeyException e)
//		{
//			Debug.errDebug("Problem initializing KeyStore: " + e.toString(), e);
//			System.exit(1);
//		}
//		
//		PlainSocketFactory plainSocketFactory = PlainSocketFactory.getSocketFactory();
//		
//		schemeRegistry.register(new Scheme("https", secureSocketFactory, 443));
//		schemeRegistry.register(new Scheme("http", plainSocketFactory, 80));
//	}
	
	
	protected void initThreads()
	{
		requestListenerThreads = new GenericProxyRequestListenerThread[maxThreads];
		for (int index = 0; index < maxThreads; index++)
		{
			GenericProxyRequestListenerThread listenerThread = createRequestListenerThread();
			listenerThread.setName("ProxyThread-" + getName() + " -- " + index);
			requestListenerThreads[index] = listenerThread;
			listenerThread.start();
		}
	}
	
	protected abstract GenericProxyRequestListenerThread createRequestListenerThread();
	
	public void startProxy()
	{
		Log.debug("Starting proxy for " + getProxyDescription());
		synchronized(runningLock)
		{
			if (running)
			{
				return;
			}
			initParams();
			try 
			{
				createSocket();
				running = true;
				initThreads();
			} 
			catch (IOException e) 
			{
				Log.error("Failed to start proxy for " + getProxyDescription(), e);
			}
		}
	}

	public boolean isRunning()
    {
    	return running;
    }

	
	public void stopProxy()
    {
		Log.debug("Stopping proxy for " + getProxyDescription());
		synchronized(runningLock)
		{
			if (!running)
			{
				Log.trace("Proxy already stopped");
				return;
			}
			Log.trace("Proxy stopped");
			running = false;
		}
		
//		ccm.shutdown();
		try
        {
			Log.trace("Trying to close server socket");
			if (serverSocket != null)
				serverSocket.close();
        }
        catch (IOException e)
        {
        	Log.error("Problem closing proxy socket: " + e.toString(), e);
        }

        
        for (GenericProxyRequestListenerThread requestListenerThread: requestListenerThreads)
		{
			Log.trace("Shutting down thread (" + requestListenerThread.getName() + ") for " + getProxyDescription());
			requestListenerThread.shutdown();
		}
	}
	
	public abstract String getName();
	public abstract boolean isSSL();

	public CustomHttpClient getProxyHttpClient()
    {
    	return proxyHttpClient;
    }
	
	public abstract AbstractProxyRequestHandler getRequestHandler(Socket socket, boolean ssl, int sslPort);

	public ProxyConfig getConfig()
    {
    	return config;
    }

	public String getProxyDescription()
	{
		return config.getBindIP() + ":" + config.getBindPort();
	}
}
