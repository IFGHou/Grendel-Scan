package com.grendelscan.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.proxy.ssl.TunneledSSLConnection;

public class SSLReverseProxyRequestListenerThread extends ReverseProxyRequestListenerThread
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLReverseProxyRequestListenerThread.class);
	
	
	public SSLReverseProxyRequestListenerThread(ServerSocket serverSocket, ReverseProxy proxy)
    {
	    super(serverSocket, proxy);
    }

	@Override
    protected void handleNewConnection(Socket socket)
	{
		TunneledSSLConnection tunneledSSLConnection = null;
		try
		{
			tunneledSSLConnection = new TunneledSSLConnection(socket, reverseProxy.getReverseProxyConfig().getWebHostname());
			HttpService httpService = MiscHttpFactory.createHttpServiceProxy(
					new ReverseProxyRequestHandler(reverseProxy));
			HttpContext context = new BasicHttpContext(null);

//			while (!Thread.interrupted() && tunneledSSLConnection.isOpen() && proxy.isRunning()
//			        && !socket.isClosed())
			{
				httpService.handleRequest(tunneledSSLConnection, context);
			}
		}
		catch (ConnectionClosedException e)
		{
			// not a problem
		}
		catch (SSLHandshakeException e)
		{
			//
		}
		catch (SocketTimeoutException e)
		{
			// 
		}
		catch (SocketException e)
		{
//			Debug.errDebug("Socket exception handling proxy request: " + e.toString(), e);
		}
        catch (IOException e)
        {
        	LOGGER.error("Problem with " + getName() + ": " + e.toString(), e);
        }
        catch (HttpException e)
        {
        	LOGGER.error("Problem with " + getName() + ": " + e.toString(), e);
        }
        catch (GeneralSecurityException e)
        {
        	LOGGER.error("Problem with " + getName() + ": " + e.toString(), e);
        }
		finally
		{
			if (tunneledSSLConnection != null)
				tunneledSSLConnection.shutdown();
		}
	}

}
