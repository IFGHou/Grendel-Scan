package com.grendelscan.proxy.ssl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.params.HttpParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLTunnelInputBuffer extends AbstractSessionInputBuffer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLTunnelInputBuffer.class);
	private InputStream sslInputStream;
	public SSLTunnelInputBuffer(InputStream sslInputStream, int buffersize, HttpParams params)
	{
		LOGGER.trace("Creating SSLTunnelInputBuffer");
		this.sslInputStream = sslInputStream;
		
		if (buffersize < 1024)
		{
			LOGGER.trace("Setting buffer size to a min of 1024");
			buffersize = 1024;
		}
		
		init(sslInputStream, buffersize, params);
		
	}
	
	@Override
	public boolean isDataAvailable(int timeout) throws IOException
	{
		LOGGER.trace("Checking for available data");
		int data = sslInputStream.available();
		LOGGER.trace("About " + data + " bytes are available");
		return data > 0;
	}
}
