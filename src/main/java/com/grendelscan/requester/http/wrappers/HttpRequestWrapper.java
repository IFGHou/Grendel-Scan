package com.grendelscan.requester.http.wrappers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.HttpProtocolParams;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.apache_overrides.requests.GenericRequestWithBody;
import com.grendelscan.utils.URIStringUtils;

// Need to take a closer look at synchronization here
public class HttpRequestWrapper extends HttpMessageWrapper
{
	private static final long serialVersionUID = -4950627342682724871L;
	private String method;
	private String uri;
	private boolean secure;
	private ProtocolVersion version;
	public transient static final ProtocolVersion DEFAULT_PROTOCL_VERSION = new ProtocolVersion("HTTP", 1, 1); 
	private String									networkHost;
	private int										networkPort;

	
	public HttpRequestWrapper(int transactionId)
	{
		super(transactionId);
		method = "GET";
		version = DEFAULT_PROTOCL_VERSION;
	}
	
	@Override
	public String toString()
	{
		return new String(getBytes());
	}

	@Override public byte[] getBytes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			out.write(method.getBytes());
			out.write(' ');
			out.write(uri.getBytes());
			out.write(' ');
			out.write(version.toString().getBytes());
			out.write(HttpConstants.CRLF_BYTES);
			out.write(super.getBytes());
		}
		catch (IOException e)
		{
			Log.error("Weird problem getting bytes from request wrapper: " + e.toString(), e);
		}
		return out.toByteArray();
	}
	
	public void setVersion(String protocol, int major, int minor)
	{
		version = new ProtocolVersion(protocol, major, minor);
	}

	public void setVersion(ProtocolVersion version)
	{
		this.version = version;
	}

	public void copyNetworkTarget(HttpRequestWrapper source)
	{
		networkHost = source.networkHost;
		networkPort = source.networkPort;
		secure = source.secure;
	}
	
	public String getAbsoluteUriString()
	{
		try
		{
			if (!URIStringUtils.getScheme(uri).isEmpty())
			{
				return uri;
			}
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		
		String absolute = secure ? "https://" : "http://";
		absolute += getHost();
		if (networkPort > 0 && ((secure && networkPort != 443) ||
			(!secure && networkPort != 80)))
		{
			absolute += ":" + networkPort;
		}
		absolute += uri;
		return absolute;
	}

	public String getMethod()
	{
		return method;
	}
	
	public HttpRequest getRequest()
	{
		HttpRequestBase request = null;
		
		request = new GenericRequestWithBody(method, uri, this);
		
		ProtocolVersion versionToUse = version;
		if (versionToUse == null)
		{
			versionToUse = HttpRequestWrapper.DEFAULT_PROTOCL_VERSION;
		}
	    HttpProtocolParams.setVersion(request.getParams(), versionToUse);


		if (headers != null)
		{
			request.setHeaders(headers.getReadOnlyHeaderArray());
		}

		return request;
	}
	
	public HttpHost getHttpHost()
	{
		String hostToUse = networkHost;
		int portToUse = networkPort;
		if (networkHost == null || networkHost.isEmpty())
		{
			hostToUse = getHost();
		}
		if (networkPort == 0)
		{
			try
			{
				portToUse = URIStringUtils.getPort(getAbsoluteUriString());
			}
			catch (URISyntaxException e)
			{
				Log.error("Very weird problem getting port number from URL", e);
			}
		}
		return new HttpHost(hostToUse, portToUse, secure ? "HTTPS" : "HTTP");
	}
	
	
	/**
	 * Checks for an absolute URL, then returns the FIRST host header value
	 * @return
	 */
	public String getHost()
	{
		String host = null;
		Header header = getHeaders().getFirstHeader("Host");
		if (header != null)
		{
			host = header.getValue();
		}

		if (host == null || host.isEmpty())
		{
			try
			{
				host = URIStringUtils.getHost(uri);
			}
			catch (URISyntaxException e)
			{
				Log.trace("No host defined. Unusual, but could be true");
			}
		}
		return host;
	}
	
	public String getQuery()
	{
		try
		{
			return URIStringUtils.getQuery(uri);
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
	}
	
	
	public String getPath()
	{
		try
		{
			return URIStringUtils.getDirectory(uri);
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
	}
	
	public String getURI()
	{
		return uri;
	}
	
//	/**
//	 * 
//	 * @return same as getHost() + ":" + getNetworkPort();
//	 */
//	public String getHostAndPort()
//	{
//		return getHost() + ":" + getNetworkPort();
//	}
	
	public HttpRequestWrapper clone(int transactionId)
	{
		HttpRequestWrapper clone = new HttpRequestWrapper(transactionId);
		super.clone(clone);
		clone.method = new String(method);
		clone.uri = new String(uri);
		clone.version = version; // ProtocolVersion is immutable, so we can reuse the object
		clone.networkHost = new String(networkHost);
		clone.networkPort = networkPort;
		clone.secure = secure;
		return clone;
	}
	
//	public void setRequest(HttpRequest request)
//	{
//		if (!((request instanceof HttpTrace) || (request instanceof HttpOptions) || 
//				(request instanceof HttpHead) || (request instanceof HttpGet) || 
//				(request instanceof HttpPut) || (request instanceof HttpPost)))
//		{
//			RuntimeException e = new RuntimeException("Unknown HttpRequest type");
//			throw e;
//		}
//		String host = request.getFirstHeader(HttpHeaders.HOST).getValue();
//		setHeaders(new HttpHeadersWrapper());
//		getHeaders().addHeaders(request.getAllHeaders());
//		uri = request.getRequestLine().getUri();
//		method = request.getRequestLine().getMethod();
//		version = request.getProtocolVersion();
//	}
//	
	/**
	 * NOTE: This will override the network host value if adjustToRelativeURI is true
	 * @param uri
	 * @param adjustToRelativeURI
	 * @throws URISyntaxException 
	 */
	public void setURI(final String uri, boolean adjustToRelativeURI)
	{
		if (adjustToRelativeURI)
		{
			String hostValue;
			try
			{
				String tmpHost = URIStringUtils.getHost(uri);
				if (!tmpHost.isEmpty())
				{
					hostValue = tmpHost;
					getHeaders().removeHeaders(HttpHeaders.HOST);
					getHeaders().addHeader(HttpHeaders.HOST, hostValue);
					this.networkHost = hostValue;
				}
				this.uri = URIStringUtils.escapeUri(URIStringUtils.getRelativeUri(uri));
				String tmpScheme = URIStringUtils.getScheme(uri);
				if (!tmpScheme.isEmpty())
				{
					secure = tmpScheme.equalsIgnoreCase("https");
				}
				int tmpPort = URIStringUtils.getPort(uri);
				if (tmpPort > 0)
				{
					this.networkPort = tmpPort;
				}
			}
			catch (URISyntaxException e)
			{
				Log.error("Illegal URL syntax: " + e.toString(), e);
				throw new IllegalArgumentException(e);
			}
		}
		else
		{
			this.uri = URIStringUtils.escapeUri(uri);
		}
	}


	public final boolean isSecure()
	{
		return secure;
	}


	public final void setSecure(boolean secure)
	{
		this.secure = secure;
	}


	public final void setMethod(String method)
	{
		this.method = method;
	}

	public final String getNetworkHost()
	{
		return networkHost;
	}

	public final void setNetworkHost(String networkHost)
	{
		this.networkHost = networkHost;
	}

	public final int getNetworkPort()
	{
		return networkPort;
	}

	public final void setNetworkPort(int networkPort)
	{
		this.networkPort = networkPort;
	}

	public final ProtocolVersion getVersion()
	{
		return version;
	}
}
