package com.grendelscan.proxy.forwardProxy;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import com.grendelscan.logging.Log;
import com.grendelscan.proxy.Destination;
import com.grendelscan.proxy.MiscHttpFactory;
import com.grendelscan.proxy.abstractProxy.AbstractProxy;
import com.grendelscan.proxy.abstractProxy.AbstractProxyRequestHandler;
import com.grendelscan.proxy.ssl.TunneledSSLConnection;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;

public class ForwardProxyRequestHandler extends AbstractProxyRequestHandler
{
	protected Socket socket;
	public ForwardProxyRequestHandler(Socket socket, AbstractProxy proxy, boolean ssl, int sslPort)
	{
		super(proxy, ssl, sslPort);
		this.socket = socket;
	}


	@Override
    protected Destination getDestination(HttpRequest request)
    {
		Header hostHeaders[] = request.getHeaders("Host");
		Destination destination = new Destination();
		if (hostHeaders.length > 0)
		{
			destination.host = hostHeaders[0].getValue();
			int colonIndex = destination.host.indexOf(":");
			if (colonIndex > 0)
			{
				destination.host = destination.host.substring(0, colonIndex);
				destination.port = Integer.valueOf(hostHeaders[0].getValue().substring(colonIndex + 1));
			}
			else
			{
				if (!ssl)
				{
					destination.port = 80;
				}
			}
		}
		else
		{
			try
			{
				destination.host = URIStringUtils.getHost(request.getRequestLine().getUri());
				destination.port = URIStringUtils.getPort(request.getRequestLine().getUri());
			}
			catch (URISyntaxException e)
			{
				IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
				Log.error(e.toString(), e);
				throw ise;
			}
		}
	    return destination;
    }




	@Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws IOException
    {
		if (request.getRequestLine().getMethod().equals("CONNECT"))
		{
			handleConnectMethod(request, response, context);
		}
		else
		{
			super.handle(request, response, context);
		}
    }

	protected void handleConnectMethod(HttpRequest request, HttpResponse response, HttpContext context)
	{
		try
		{
			if (!Scan.getScanSettings().getUrlFilters().isUriAllowed("https://" + request.getRequestLine().getUri()) && ! Scan.getScanSettings().isAllowAllProxyRequests())
			{
				forbiddenMessage(request, response);
				return;
			}
			
			
			/*
			 * This is a little dirty. Normally, the response is sent back
			 * when this method returns. However, since this method is
			 * handling the SSL connection, we need to manually send an OK.
			 * I could create a response object, but the response is so 
			 * simple, it is easier just to write it manually.
			 */
			socket.getOutputStream().write("HTTP/1.0 200 Ok\n\n".getBytes(StringUtils.getDefaultCharset()));
			
			
			String uri = request.getRequestLine().getUri();
			int colonPosition = uri.indexOf(':');
			int sslPort;
			String sslHostname;
			if (colonPosition > 0)
			{
				sslPort = Integer.valueOf(uri.substring(colonPosition + 1, uri.length()));
				sslHostname = (String) uri.subSequence(0, colonPosition);
			}
			else
			{
				sslPort = 443;
				sslHostname = uri;
			}

			TunneledSSLConnection tunneledSSLConnection = new TunneledSSLConnection(socket, sslHostname);

			
			/*
			 * This will create a sub-HttpProxyRequestHandler to handle the
			 * requests that come through the SSL connection. In theory,
			 * those requests could even be other CONNECTs
			 */
			HttpService httpService = MiscHttpFactory.createHttpServiceProxy(proxy.getRequestHandler(socket, true, sslPort));
			
			/*
			 * Normally, this would be in the worker thread, but we don't
			 * need to create a new thread since the worker thread created
			 * for the CONNECT transaction will work just fine. Eventually,
			 * I should consolidate the two code areas.
			 */
			try
			{
				int requestCount = 0;
				while (!Thread.interrupted() 
						&& tunneledSSLConnection.isOpen() 
						&& proxy.isRunning()
//				        && !socket.isClosed()
				        )
				{
					httpService.handleRequest(tunneledSSLConnection, context);
					requestCount++;
					Log.trace("Processed request number " + requestCount);
				}
			}
			catch (ConnectionClosedException e)
			{
				Log.trace("Connection closed", e);
			}
			catch (SocketException e)
			{
				Log.info("SocketException handling proxy request: " + e.toString(), e);
			}
			catch (SocketTimeoutException e)
			{
				Log.info("SocketTimeoutException handling proxy request: " + e.toString(), e);
			}
//			catch (Exception e)
//			{
//				if (e.toString().equals("Received fatal alert: certificate_unknown"))
//				{
//					Log.warn("Client refused to accept our SSL cert");
//				}
//				else
//				{
//					Log.error("Exception handling proxy request: " + e.toString(), e);
//				}
//			}
			finally
			{
				/*
				 * In theory, I think that another CONNECT could be
				 * processed on the same socket, but I'm shutting it down
				 * anyway.
				 */
				tunneledSSLConnection.shutdown();
			}
		}
		catch (SSLHandshakeException e)
		{
			Log.warn("Problem with SSL handshake. This is probably due to an invalid CA: " + e.toString(), e);
		}
		catch (Exception e)
		{
			Log.error("Problem with SSL proxy: " + e.toString(), e);
			// String errorText = "Problem with "
			// return some kind of error page
		}
	}

}
