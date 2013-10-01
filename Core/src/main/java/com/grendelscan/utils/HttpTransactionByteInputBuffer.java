/**
 * 
 */
package com.grendelscan.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.params.BasicHttpParams;

/**
 * @author david
 *
 */
public class HttpTransactionByteInputBuffer extends AbstractSessionInputBuffer
{
	private ByteArrayInputStream inputStream;
	
	public HttpTransactionByteInputBuffer(byte[] data)
	{
		inputStream = new ByteArrayInputStream(data);
		
		init(inputStream, 1000, new BasicHttpParams());
	}

	/* (non-Javadoc)
	 * @see org.apache.http.io.SessionInputBuffer#isDataAvailable(int)
	 */
	@Override
	public boolean isDataAvailable(int timeout) throws IOException
	{
		return inputStream.available() > 0;
	}

	public final ByteArrayInputStream getInputStream()
	{
		return inputStream;
	}

}
