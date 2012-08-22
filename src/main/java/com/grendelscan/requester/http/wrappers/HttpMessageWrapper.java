package com.grendelscan.requester.http.wrappers;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.HttpConstants;

public abstract class HttpMessageWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected byte[] body;
	protected HttpHeadersWrapper headers;
	protected int transactionId;
	
	protected HttpMessageWrapper()
	{
		body = new byte[]{};
	}
	
	public HttpMessageWrapper(int transactionId)
    {
		this();
		headers = new HttpHeadersWrapper();
		this.transactionId = transactionId;
    }

	protected void clone(HttpMessageWrapper target)
	{
		target.body = body.clone();
		target.headers = headers.clone();
	}
	
	@Override
	public String toString()
	{
		return new String(getBytes());
	}
	
	protected byte[] getBytes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			out.write(headers.getBytes());
			out.write(HttpConstants.CRLF_BYTES);
			out.write(body);
		}
		catch (IOException e)
		{
			Log.error("Weird problem getting bytes from message wrapper: " + e.toString(), e);
		}
		return out.toByteArray();
	}
	
	public final byte[] getBody()
	{
		return body;
	}

	public final void setBody(byte[] body)
	{
		if (body == null)
		{
			this.body = new byte[0];
		}
		else
		{
			this.body = body;
		}
	}

	public final HttpHeadersWrapper getHeaders()
	{
		return headers;
	}

	public final void setHeaders(HttpHeadersWrapper headers)
	{
		this.headers = headers;
	}
	
}
