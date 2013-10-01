package com.grendelscan.commons.http;
//package com.grendelscan.commons.http;
//
//import java.io.Serializable;
//import java.util.Iterator;
//
//import org.apache.http.Header;
//import org.apache.http.HeaderIterator;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpRequest;
//import org.apache.http.ProtocolVersion;
//import org.apache.http.params.HttpParams;
//
//import com.grendelscan.commons.http.serializable.SerializableHeaderGroup;
//import com.grendelscan.commons.http.serializable.SerializableHttpHeader;
//import com.grendelscan.commons.http.serializable.SerializableRequestLine;
//import com.grendelscan.commons.http.URIStringUtils;
//
//public class UnvalidatedHttpRequest implements HttpRequest, Serializable
//{
//	/**
//     * 
//     */
//	private static final long serialVersionUID = 7737605346178523306L;
//	protected SerializableRequestLine requestLine;
//	protected transient HttpHost target;
//	protected String targetedHost;
//	protected String scheme;
//	protected int targetedPort;
//	protected boolean ssl;
//	protected SerializableHeaderGroup headergroup;
//	transient protected HttpParams params;
//	
//	public UnvalidatedHttpRequest(String method, String uri, ProtocolVersion version, String host, int port, boolean ssl)
//	{
//		this.ssl = ssl;
//		targetedHost = host;
//		targetedPort = port;
//		headergroup = new SerializableHeaderGroup();
//		requestLine = new SerializableRequestLine(method, URIStringUtils.escapeUri(uri), version);
//		if (ssl)
//		{
//			scheme = "https";
//		}
//		else
//		{
//			scheme = "http";
//		}
//		
//		target = new HttpHost(host, port, scheme);
//	}
//	
//	public UnvalidatedHttpRequest(String method, String uri, String host, int port, boolean ssl)
//	{
//		this(method, uri, "HTTP", 1, 1, host, port, ssl);
//	}
//	
//	public UnvalidatedHttpRequest(String method, String uri, String protocol, int majorProtocolVersion,
//	        int minorProtocolVersion, String host, int port, boolean ssl)
//	{
//		this(method, uri, new ProtocolVersion(protocol.toUpperCase(), majorProtocolVersion, minorProtocolVersion),
//		        host, port, ssl);
//	}
//	
//	protected UnvalidatedHttpRequest()
//	{
//	}
//	
//	public UnvalidatedHttpRequest clone()
//	{
//		UnvalidatedHttpRequest clone = new UnvalidatedHttpRequest();
//		clone.headergroup = headergroup.clone();
//		clone.requestLine = requestLine.clone();
//		clone.targetedHost = targetedHost;
//		clone.scheme = scheme;
//		clone.targetedPort = targetedPort;
//		clone.target = new HttpHost(targetedHost, targetedPort, scheme);
//		clone.ssl = ssl;
//
//		return clone;
//	}
//	
//	public void addHeader(Header header)
//	{
//		headergroup.addHeader(header);
//	}
//	
//	public void addHeader(String name, String value)
//	{
//		this.addHeader(new SerializableHttpHeader(name, value));
//	}
//	
//	public void addHeaders(Header[] headers)
//	{
//		for (Header header: headers)
//		{
//			addHeader(header);
//		}
//	}
//	
//	public boolean containsHeader(String name)
//	{
//		return headergroup.containsHeader(name);
//	}
//	
//	public Header[] getAllHeaders()
//	{
//		return headergroup.getAllHeaders();
//	}
//	
//	public Header getCondensedHeader(String name)
//	{
//		return headergroup.getCondensedHeader(name);
//	}
//	
//	public Header getFirstHeader(String name)
//	{
//		return headergroup.getFirstHeader(name);
//	}
//	
//	public Header[] getHeaders(String name)
//	{
//		return headergroup.getHeaders(name);
//	}
//	
//	public Header getLastHeader(String name)
//	{
//		return headergroup.getLastHeader(name);
//	}
//	
//	public HttpParams getParams()
//	{
//		return params;
//	}
//	
//	public ProtocolVersion getProtocolVersion()
//	{
//		return requestLine.getProtocolVersion();
//	}
//	
//	public SerializableRequestLine getRequestLine()
//	{
//		return requestLine;
//	}
//	
//	public HttpHost getTarget()
//	{
//		if (target == null)
//		{
//			target = new HttpHost(targetedHost, targetedPort, scheme);
//		}
//		return target;
//	}
//	
//	public String getTargetedHost()
//	{
//		return targetedHost;
//	}
//	
//	public int getTargetedPort()
//	{
//		return targetedPort;
//	}
//	
//	public HeaderIterator headerIterator()
//	{
//		return headergroup.iterator();
//	}
//	
//	public HeaderIterator headerIterator(String name)
//	{
//		return headergroup.iterator(name);
//	}
//	
//	public boolean isSsl()
//	{
//		return ssl;
//	}
//	
//	public void removeHeader(Header header)
//	{
//		headergroup.removeHeader(header);
//	}
//	
//	public void removeHeaders(String name)
//	{
//		for (Iterator i = headergroup.iterator(); i.hasNext();)
//		{
//			Header header = (Header) i.next();
//			if (name.equalsIgnoreCase(header.getName()))
//			{
//				i.remove();
//			}
//		}
//	}
//	
//	public void setHeader(Header header)
//	{
//		headergroup.updateHeader(header);
//	}
//	
//	public void setHeader(String name, String value)
//	{
//		if (name == null)
//		{
//			throw new IllegalArgumentException("Header name may not be null");
//		}
//		headergroup.updateHeader(new SerializableHttpHeader(name, value));
//	}
//	
//	public void setHeaders(Header[] headers)
//	{
//		headergroup.setHeaders(headers);
//	}
//	
//	public void setParams(HttpParams params)
//	{
//		this.params = params;
//	}
//	
//	public void setRequestLine(SerializableRequestLine requestLine)
//	{
//		this.requestLine = requestLine;
//	}
//	
//	public void setSsl(boolean ssl)
//	{
//		this.ssl = ssl;
//	}
//	
//	public void setTarget(HttpHost target)
//	{
//		this.target = target;
//	}
//	
//	public void setTargetedHost(String targetedHost)
//	{
//		this.targetedHost = targetedHost;
//	}
//	
//	public void setTargetedPort(int targetedPort)
//	{
//		this.targetedPort = targetedPort;
//	}
//	
//	public void updateHeader(Header header)
//	{
//		headergroup.updateHeader(header);
//	}
//	
//	@Override
//    public String toString()
//	{
//		String string = "";
//		string += requestLine.toString() + "\n";
//		for(Header header: headergroup.getAllHeaders())
//		{
//			string += header.toString() + "\n";
//		}
//		
//		return string;
//	}
//}
