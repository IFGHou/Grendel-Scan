/**
 * 
 */
package com.grendelscan.commons.http;

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
    private final ByteArrayInputStream inputStream;

    public HttpTransactionByteInputBuffer(final byte[] data)
    {
        inputStream = new ByteArrayInputStream(data);

        init(inputStream, 1000, new BasicHttpParams());
    }

    public final ByteArrayInputStream getInputStream()
    {
        return inputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.http.io.SessionInputBuffer#isDataAvailable(int)
     */
    @Override
    public boolean isDataAvailable(final int timeout) throws IOException
    {
        return inputStream.available() > 0;
    }

}
