package com.grendelscan.proxy.reverseProxy;

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

import com.grendelscan.logging.Log;
import com.grendelscan.proxy.MiscHttpFactory;
import com.grendelscan.proxy.ssl.TunneledSSLConnection;

public class SSLReverseProxyRequestListenerThread extends ReverseProxyRequestListenerThread
{
	
	
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
        	Log.error("Problem with " + getName() + ": " + e.toString(), e);
        }
        catch (HttpException e)
        {
        	Log.error("Problem with " + getName() + ": " + e.toString(), e);
        }
        catch (GeneralSecurityException e)
        {
        	Log.error("Problem with " + getName() + ": " + e.toString(), e);
        }
		finally
		{
			if (tunneledSSLConnection != null)
				tunneledSSLConnection.shutdown();
		}
	}

}
