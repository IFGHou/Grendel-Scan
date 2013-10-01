package com.grendelscan.proxy.ssl;

/*
 * $HeadURL:
 * https://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/module-nio/src/main/java/org/apache/http/impl/nio/reactor/SSLIOSession.java $
 * $Revision: 600151 $ $Date: 2007-12-01 08:57:51 -0700 (Sat, 01 Dec 2007) $
 * 
 * ==================================================================== Licensed
 * to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The ASF licenses this file to you
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Apache Software Foundation. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.impl.AbstractHttpServerConnection;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.proxy.MiscHttpFactory;

public class TunneledSSLConnection extends AbstractHttpServerConnection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TunneledSSLConnection.class);
	private static CertificateAuthority ca;
	
	private boolean open;
	private final Socket socket;
	private final SSLSocket sslSocket;
	private final String destinationHostname;
	
	private final InputStream sslInputStream;
	private final OutputStream sslOutputStream;
	private final SSLTunnelInputBuffer sslTunnelInputBuffer;
	private final SSLTunnelOutputBuffer sslTunnelOutputBuffer;

	
	public TunneledSSLConnection(Socket socket, String destinationHostname) 
			throws SSLException, IOException, GeneralSecurityException
	{
		LOGGER.trace("Instantiating TunneledSSLConnection");
		this.destinationHostname = destinationHostname;
		this.socket = socket;
		if (socket == null)
		{
			IllegalArgumentException e = new IllegalArgumentException("socket cannot be null");
			LOGGER.error("Socket cannot be null", e);
			throw e;
		}

		if (destinationHostname == null)
		{
			IllegalArgumentException e = new IllegalArgumentException("destinationHostname cannot be null");
			LOGGER.error("destinationHostname cannot be null", e);
			throw e;
		}

		SSLSocketFactory sslSocketFactory = initializeSSLFactory();
		HttpParams params = MiscHttpFactory.createDefaultHttpProxyParams();
		
		int buffersize = HttpConnectionParams.getSocketBufferSize(params);
		sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, socket.getInetAddress().getHostAddress(), 
				socket.getPort(), true);
		sslSocket.setUseClientMode(false);
		
		sslInputStream = sslSocket.getInputStream();
		sslTunnelInputBuffer = new SSLTunnelInputBuffer(sslInputStream, buffersize, params);
		
		sslOutputStream = sslSocket.getOutputStream();
		sslTunnelOutputBuffer = new SSLTunnelOutputBuffer(sslOutputStream, buffersize, params);
		
		// This is the real important part where we identify the buffers to the parent
		init(sslTunnelInputBuffer, sslTunnelOutputBuffer, params);
		open = true;
	}
	
	private SSLSocketFactory initializeSSLFactory() throws GeneralSecurityException, IOException
	{
		LOGGER.trace("Initializing SSL for tunnel");
		if (ca == null)
		{
			LOGGER.trace("Getting the static CA");
			ca = CertificateAuthority.getCertificateAuthority();
		}
        
		KeyManagerFactory kmfactory;
		KeyStore keystore = ca.getKeyStore(destinationHostname);
        
        kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, ca.getKeyPassword());
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmfactory.getKeyManagers(), null, null);
		return sslContext.getSocketFactory();
	}
	
//	private HttpParams initializeHttpParams()
//	{
//		HttpParams params = new BasicHttpParams();
//		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 500)
//			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
//			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
//			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
//		return params;
//	}

	@Override
	public int getSocketTimeout()
	{
		int timeout = -1;
		LOGGER.trace("Getting socket timeout");
		if (socket != null)
		{
			try
			{
				timeout = sslSocket.getSoTimeout();
			}
			catch (SocketException e)
			{
				LOGGER.warn("Error trying to get socket timeout: " + e.getMessage(), e);
			}
		}
		else
		{
			LOGGER.debug("Socket is null; no socket timeout");
		}
		return timeout;
	}
	
	@Override
	public boolean isOpen()
	{
		try
		{
			assertOpen();
		}
		catch(IllegalStateException e)
		{
			LOGGER.trace("Gently caught an unexpected close: " + e.toString(), e);
		}
		return open;
	}
	
	@Override
	public void setSocketTimeout(int timeout)
	{
		LOGGER.debug("Setting socket timeout to " + timeout);
		assertOpen();
		try
		{
			socket.setSoTimeout(timeout);
			sslSocket.setSoTimeout(timeout);
		}
		catch (SocketException e)
		{
			LOGGER.warn("Error trying to set socket timeout to " + timeout + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public void shutdown()
	{
		close();
	}
	@Override
	public void close()
	{
		LOGGER.debug("Shutting down socket");
		open = false;
		try
		{
			sslSocket.close();
		}
		catch (IOException e)
		{
			LOGGER.trace("Problem closing ssl socket: " + e.toString(), e);
		}
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			LOGGER.trace("Problem closing socket: " + e.toString(), e);
		}
	}
	
	
	@Override
	protected void assertOpen() throws IllegalStateException
	{
		if (!open)
		{
			String message = "assertOpen failed: tunnel is not open";
			LOGGER.warn(message);
			throw new IllegalStateException(message);
		}
		
		if (socket.isClosed() || ! socket.isConnected())
		{
			String message = "assertOpen failed: socket is unexpectidly not open"; 
			LOGGER.warn(message);
			open = false;
			try
			{
				sslSocket.close();
			}
			catch (IOException e)
			{
				LOGGER.trace("Problem closing ssl socket: " + e.toString(), e);
			}
			throw new IllegalStateException(message);
		}

		if (sslSocket.isClosed() || ! sslSocket.isConnected())
		{
			String message = "assertOpen failed: sslSocket is unexpectidly not open"; 
			LOGGER.warn(message);
			open = false;
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				LOGGER.trace("Problem closing socket: " + e.toString(), e);
			}
			throw new IllegalStateException(message);
		}
	}
}
