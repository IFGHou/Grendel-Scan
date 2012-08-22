///**
// * 
// */
//package com.grendelscan.requester.http.apache_overrides.requests;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import javax.management.RuntimeErrorException;
//
//import org.apache.commons.lang.NotImplementedException;
//import org.apache.http.Header;
//import org.apache.http.RequestLine;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.message.BasicRequestLine;
//
//import com.grendelscan.logging.Log;
//import com.grendelscan.requester.http.wrappers.HttpRequestWrapper;
//
///**
// * @author david
// *
// */
//public class GenericRequest extends HttpRequestBase
//{
//
//	private String method;
//	private String stringURI;
//	private HttpRequestWrapper wrapper;
//	
//	/**
//	 * 
//	 */
//	public GenericRequest(String method, String stringURI, HttpRequestWrapper wrapper)
//	{
//		this.method = method;
//		this.stringURI = stringURI;
//		this.wrapper = wrapper;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.apache.http.client.methods.HttpRequestBase#getMethod()
//	 */
//	@Override
//	public String getMethod()
//	{
//		return method;
//	}
//
//	@Override
//	public URI getURI()
//	{
//		try
//		{
//			return new URI(stringURI);
//		}
//		catch (URISyntaxException e)
//		{
//			Log.error("URI problem in request: " + e.toString(), e);
//			throw new RuntimeException(e);
//		}
//	}
//
//	public final String getStringURI()
//	{
//		return stringURI;
//	}
//
//	@Override
//	public RequestLine getRequestLine()
//	{
//        return new BasicRequestLine(method, stringURI, getProtocolVersion());
//	}
//
//	@Override
//	public void setURI(@SuppressWarnings("unused") URI uri)
//	{
//		throw new NotImplementedException("Don't use real URIs here");
//	}
//
//
//	@Override
//	public void addHeader(Header header)
//	{
//		wrapper.getHeaders().addHeader(header);
//		super.addHeader(header);
//	}
//
//	@Override
//	public void addHeader(String name, String value)
//	{
//		wrapper.getHeaders().addHeader(name, value);
//		super.addHeader(name, value);
//	}
//
//	@Override
//	public void setHeader(Header header)
//	{
//		wrapper.getHeaders().updateHeader(header);
//		super.setHeader(header);
//	}
//
//	@Override
//	public void setHeader(String name, String value)
//	{
//		wrapper.getHeaders().updateHeader(name, value);
//		super.setHeader(name, value);
//	}
//
//	@Override
//	public void setHeaders(Header[] headers)
//	{
//		wrapper.getHeaders().setHeaders(headers);
//		super.setHeaders(headers);
//	}
//
//	@Override
//	public void removeHeader(Header header)
//	{
//		wrapper.getHeaders().removeHeader(header);
//		super.removeHeader(header);
//	}
//
//	@Override
//	public void removeHeaders(String name)
//	{
//		wrapper.getHeaders().removeHeaders(name);
//		super.removeHeaders(name);
//	}
//
//}
