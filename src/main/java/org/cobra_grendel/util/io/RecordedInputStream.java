/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Apr 15, 2005
 */
package org.cobra_grendel.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an InputStream and records all of the bytes read. This stream supports
 * mark() and reset().
 * <p>
 * Note: Buffered streams should wrap this class as opposed to the other way
 * around.
 * 
 * @author J. H. S.
 */
public class RecordedInputStream extends InputStream
{
	private final InputStream delegate;
	private boolean hasReachedEOF = false;
	private int markPosition = -1;
	private int readPosition = -1;
	private byte[] resetBuffer = null;
	private final ByteArrayOutputStream store = new ByteArrayOutputStream();
	
	/**
	 * 
	 */
	public RecordedInputStream(InputStream delegate)
	{
		super();
		this.delegate = delegate;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException
	{
		return delegate.available();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		delegate.close();
	}
	
	public byte[] getBytesRead()
	{
		return store.toByteArray();
	}
	
	public String getString(String encoding) throws java.io.UnsupportedEncodingException
	{
		byte[] bytes = store.toByteArray();
		return new String(bytes, encoding);
	}
	
	public boolean hasReachedEOF()
	{
		return hasReachedEOF;
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
		markPosition = store.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported()
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException
	{
		if ((readPosition != -1) && (readPosition < resetBuffer.length))
		{
			int b = resetBuffer[readPosition];
			readPosition++;
			return b;
		}
		else
		{
			int b = delegate.read();
			if (b != -1)
			{
				store.write(b);
			}
			else
			{
				hasReachedEOF = true;
			}
			return b;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException
	{
		if ((readPosition != -1) && (readPosition < resetBuffer.length))
		{
			int minLength = Math.min(resetBuffer.length - readPosition, length);
			System.arraycopy(resetBuffer, readPosition, buffer, offset, minLength);
			readPosition += minLength;
			return minLength;
		}
		else
		{
			int numRead = delegate.read(buffer, offset, length);
			if (numRead != -1)
			{
				store.write(buffer, offset, numRead);
			}
			else
			{
				hasReachedEOF = true;
			}
			return numRead;
		}
	}
	
	@Override
	public synchronized void reset() throws IOException
	{
		int mp = markPosition;
		byte[] wholeBuffer = store.toByteArray();
		byte[] resetBuffer = new byte[wholeBuffer.length - mp];
		System.arraycopy(wholeBuffer, mp, resetBuffer, 0, resetBuffer.length);
		this.resetBuffer = resetBuffer;
		readPosition = 0;
	}
}
