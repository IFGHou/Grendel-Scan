package com.grendelscan.proxy.ssl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.params.HttpParams;

import com.grendelscan.logging.Log;

public class SSLTunnelInputBuffer extends AbstractSessionInputBuffer
{
	private InputStream sslInputStream;
	public SSLTunnelInputBuffer(InputStream sslInputStream, int buffersize, HttpParams params)
	{
		Log.trace("Creating SSLTunnelInputBuffer");
		this.sslInputStream = sslInputStream;
		
		if (buffersize < 1024)
		{
			Log.trace("Setting buffer size to a min of 1024");
			buffersize = 1024;
		}
		
		init(sslInputStream, buffersize, params);
		
	}
	
	@Override
	public boolean isDataAvailable(int timeout) throws IOException
	{
		Log.trace("Checking for available data");
		int data = sslInputStream.available();
		Log.trace("About " + data + " bytes are available");
		return data > 0;
	}
}
