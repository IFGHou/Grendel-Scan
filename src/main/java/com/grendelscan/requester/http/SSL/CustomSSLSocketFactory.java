package com.grendelscan.requester.http.SSL;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Basically a modified version of org.apache.http.conn.ssl.SSLSocketFactory
 * 
 * @author David Byrne
 * 
 */
public class CustomSSLSocketFactory implements LayeredSocketFactory
{


	private X509HostnameVerifier hostnameVerifier = new CustomSSLVerifier();

	private final javax.net.ssl.SSLSocketFactory socketfactory;
	private final SSLContext sslcontext;



	public CustomSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException
	{
		SecureRandom random = new SecureRandom();
		TrustManager[] trustmanagers = null;
		trustmanagers = createTrustManagers();

		sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, trustmanagers, random);
		socketfactory = sslcontext.getSocketFactory();
	}

	private TrustManager[] createTrustManagers()
	{
		TrustManager tms[] = {new CustomX509TrustManager()};
		return tms;
	}

	// non-javadoc, see interface org.apache.http.conn.SocketFactory
	@Override
	public Socket connectSocket(final Socket sock, final String host, final int port, final InetAddress localAddress,
	        int localPort, final HttpParams params) throws IOException
	{
		if (host == null)
		{
			throw new IllegalArgumentException("Target host may not be null.");
		}
		if (params == null)
		{
			throw new IllegalArgumentException("Parameters may not be null.");
		}

		// resolve the target hostname first
		final InetSocketAddress target = new InetSocketAddress(host, port);

		SSLSocket sslSocket = (SSLSocket) ((sock != null) ? sock : createSocket());

		if ((localAddress != null) || (localPort > 0))
		{

			// we need to bind explicitly
			if (localPort < 0)
			{
				localPort = 0; // indicates "any"
			}

			InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
			sslSocket.bind(isa);
		}

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		sslSocket.connect(target, connTimeout);
		sslSocket.setSoTimeout(soTimeout);
		try
		{
			hostnameVerifier.verify(host, sslSocket);
			// verifyHostName() didn't blowup - good!
		}
		catch (IOException iox)
		{
			// close the socket before re-throwing the exception
			try
			{
				sslSocket.close();
			}
			catch (Exception x)
			{ /* ignore */
			}
			throw iox;
		}

		return sslSocket;
	}

	// non-javadoc, see interface org.apache.http.conn.SocketFactory
	@Override
	public Socket createSocket() throws IOException
	{

		// the cast makes sure that the factory is working as expected
		return socketfactory.createSocket();
	}

	// non-javadoc, see interface LayeredSocketFactory
	@Override
	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose)
	        throws IOException, UnknownHostException
	{
		SSLSocket sslSocket = (SSLSocket) socketfactory.createSocket(socket, host, port, autoClose);
		hostnameVerifier.verify(host, sslSocket);
		// verifyHostName() didn't blowup - good!
		return sslSocket;
	}

	public HostnameVerifier getHostnameVerifier()
	{
		return hostnameVerifier;
	}

	/**
	 * Checks whether a socket connection is secure. This factory creates
	 * TLS/SSL socket connections which, by default, are considered secure.
	 * <br/> Derived classes may override this method to perform runtime checks,
	 * for example based on the cypher suite.
	 * 
	 * @param sock
	 *            the connected socket
	 * 
	 * @return <code>true</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if the argument is invalid
	 */
	@Override
	public boolean isSecure(Socket sock) throws IllegalArgumentException
	{

		if (sock == null)
		{
			throw new IllegalArgumentException("Socket may not be null.");
		}
		// This instanceof check is in line with createSocket() above.
		if (!(sock instanceof SSLSocket))
		{
			throw new IllegalArgumentException("Socket not created by this factory.");
		}
		// This check is performed last since it calls the argument object.
		if (sock.isClosed())
		{
			throw new IllegalArgumentException("Socket is closed.");
		}

		return true;

	} // isSecure

	public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier)
	{
		if (hostnameVerifier == null)
		{
			throw new IllegalArgumentException("Hostname verifier may not be null");
		}
		this.hostnameVerifier = hostnameVerifier;
	}

}
