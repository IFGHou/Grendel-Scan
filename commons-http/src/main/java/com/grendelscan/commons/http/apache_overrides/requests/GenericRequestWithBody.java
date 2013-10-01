/**
 * 
 */
package com.grendelscan.commons.http.apache_overrides.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicRequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;

/**
 * @author david
 *
 */
public class GenericRequestWithBody extends HttpEntityEnclosingRequestBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericRequestWithBody.class);

	private String method;
	private String stringURI;
	private HttpRequestWrapper wrapper;

	public GenericRequestWithBody(String method, String stringURI, HttpRequestWrapper wrapper)
	{
		this.method = method;
		this.wrapper = wrapper;
		this.stringURI = stringURI;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.client.methods.HttpRequestBase#getMethod()
	 */
	@Override
	public String getMethod()
	{
		return method;
	}

	@Override
	public URI getURI()
	{
		URI u = null;
		try
		{
			u = new URI(stringURI);
		}
		catch (URISyntaxException e)
		{
			LOGGER.error("Illegal URI in request. This method should only be called for the caching client: " + stringURI, e);
		}
		return u;
	}

	public final String getStringURI()
	{
		return stringURI;
	}

	@Override
	public RequestLine getRequestLine()
	{
        return new BasicRequestLine(method, stringURI, getProtocolVersion());
	}


	@Override
	public void addHeader(Header header)
	{
		wrapper.getHeaders().addHeader(header);
		super.addHeader(header);
	}

	@Override
	public void addHeader(String name, String value)
	{
		wrapper.getHeaders().addHeader(name, value);
		super.addHeader(name, value);
	}

	@Override
	public void setHeader(Header header)
	{
		wrapper.getHeaders().updateHeader(header);
		super.setHeader(header);
	}

	@Override
	public void setHeader(String name, String value)
	{
		wrapper.getHeaders().updateHeader(name, value);
		super.setHeader(name, value);
	}

	@Override
	public void setHeaders(Header[] headers)
	{
		wrapper.getHeaders().setHeaders(headers);
		super.setHeaders(headers);
	}

	@Override
	public void removeHeader(Header header)
	{
		wrapper.getHeaders().removeHeader(header);
		super.removeHeader(header);
	}

	@Override
	public void removeHeaders(String name)
	{
		wrapper.getHeaders().removeHeaders(name);
		super.removeHeaders(name);
	}


	@Override
	public void setEntity(@SuppressWarnings("unused") HttpEntity entity)
	{
		throw new NotImplementedException("Don't set the entity");
	}

	@Override
	public void setURI(@SuppressWarnings("unused") URI uri)
	{
		throw new NotImplementedException("Don't use real URIs here");
	}

	@Override
	public HttpEntity getEntity()
	{
		byte body[] = wrapper.getBody();
		if (body == null)
		{
			return null;
		}
		return new ByteArrayEntity(body);
	}
}
