/**
 * 
 */
package com.grendelscan.requester.http.wrappers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableHttpHeader;

/**
 * @author david
 *
 */
public class HttpHeadersWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected ArrayList<SerializableHttpHeader> headers;
	public transient static final String URL_ENCODED_MIME = "application/x-www-form-urlencoded";
	
	public HttpHeadersWrapper()
	{
		headers = new ArrayList<SerializableHttpHeader>();
	}

	public HttpHeadersWrapper(Header[] headers)
	{
		this();
		addHeaders(headers);
	}

	public List<SerializableHttpHeader> getReadOnlyHeaders()
	{
		return new ArrayList<SerializableHttpHeader>(headers);
	}
	
	@Override
	public HttpHeadersWrapper clone()
	{
		HttpHeadersWrapper target = new HttpHeadersWrapper();
		target.headers = new ArrayList<SerializableHttpHeader>(headers.size());
		for(SerializableHttpHeader header: headers)
		{
			target.headers.add(header.clone());
		}
		return target;
	}


	public void addHeader(String name, String value)
	{
		addHeader(new SerializableHttpHeader(name, value));
	}
	
	public void addHeader(Header header)
	{
		headers.add(SerializableHttpHeader.convertToSerializableHeader(header));
	}
	
	public void updateHeader(String name, String value)
	{
		updateHeader(new SerializableHttpHeader(name, value));
	}

	
	public void updateHeader(Header header)
	{
		Header existing = getFirstHeader(header.getName());
		if (existing == null)
		{
			addHeader(header);
		}
		else
		{
			int pos = headers.indexOf(existing);
			headers.set(pos, new SerializableHttpHeader(header));
		}
	}
	
	public void removeHeader(Header header)
	{
		headers.remove(header);
	}

	public void setHeaders(Header newHeaders[])
	{
		headers.clear();
		addHeaders(newHeaders);
	}
	
	public void addHeaders(Header newHeaders[])
	{
        for (int index = 0; index < newHeaders.length; index++)
        {
	        addHeader(newHeaders[index]);
        }
	}

	public Header getLastHeader(String headerName)
    {
		for (ListIterator<SerializableHttpHeader> iterator = headers.listIterator(headers.size()); iterator.hasPrevious();)
		{
			SerializableHttpHeader header = iterator.previous();
			
	        if (header.getName().equalsIgnoreCase(headerName))
	        {
	        	return header;
	        }
        }
		return null;
    }

    public List<SerializableHttpHeader> getHeaders(String headerName)
    {
    	List<SerializableHttpHeader> tmpHeaders = new ArrayList<SerializableHttpHeader>(1);
		for (SerializableHttpHeader header: headers)
		{
			if (header.getName().equalsIgnoreCase(headerName))
			{
				tmpHeaders.add(header);
			}
		}
		return tmpHeaders;
    }
    
    public void clearHeaders()
    {
    	headers.clear();
    }
    
    public void removeHeaders(String headerName)
    {
		List<SerializableHttpHeader> tmpHeaders = new ArrayList<SerializableHttpHeader>(headers);
		for(SerializableHttpHeader header: tmpHeaders)
		{
			if (header.getName().equalsIgnoreCase(headerName))
			{
				headers.remove(header);
			}
		}
    }

    public String getMimeType()
    {
		Header header = getFirstHeader(HttpHeaders.CONTENT_TYPE);
    	if (header == null)
    	{
    		return ""; 
    	}
    	String tokens[] = header.getValue().split(";");
   		return tokens[0];
    }
    
    public String getContentTypeHeader()
    {
    	Header h = getFirstHeader(HttpHeaders.CONTENT_TYPE);
		if (h == null)
		{
			return "";
		}
    	return h.getValue();
    }
    

    public String getCharacterSet()
	{
		Header header = getFirstHeader(HttpHeaders.CONTENT_TYPE);
    	if (header == null)
    	{
    		return "ISO-8859-1"; 
    	}
    	String tokens[] = header.getValue().split(";");
    	if (tokens.length > 1)
    	{
    		String charsetParam = tokens[1];
    		return charsetParam.substring(charsetParam.indexOf('=') + 1);
    	}
    	return "ISO-8859-1";
	}

    
    @Override
	public String toString()
    {
    	return new String(getBytes());
    }
    
    public byte[] getBytes()
    {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (SerializableHttpHeader header: headers)
        {
        	try
			{
				out.write(header.getBytes());
	        	out.write(HttpConstants.CRLF_BYTES);
			}
			catch (IOException e)
			{
				Log.error("Weird problem getting bytes for headers: " + e.toString(), e);
			}
        }
    	return out.toByteArray();
    }
    

    public Header getFirstHeader(String headerName)
    {
    	for (SerializableHttpHeader header: headers)
        {
	        if (header.getName().equalsIgnoreCase(headerName))
	        {
		        return header;
	        }
        }
		return null;
    }

    public Header[] getReadOnlyHeaderArray()
    {
    	return headers.toArray(new Header[0]);
    }
    
}
