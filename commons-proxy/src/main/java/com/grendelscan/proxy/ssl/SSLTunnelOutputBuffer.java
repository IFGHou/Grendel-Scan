package com.grendelscan.proxy.ssl;

import java.io.OutputStream;

import org.apache.http.impl.io.AbstractSessionOutputBuffer;
import org.apache.http.params.HttpParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLTunnelOutputBuffer extends AbstractSessionOutputBuffer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLTunnelOutputBuffer.class);
	
	public SSLTunnelOutputBuffer(OutputStream sslOutputStream, int buffersize, HttpParams params)
	{
		LOGGER.trace("Creating SSL output buffer");
		init(sslOutputStream, buffersize, params);
	}
}
