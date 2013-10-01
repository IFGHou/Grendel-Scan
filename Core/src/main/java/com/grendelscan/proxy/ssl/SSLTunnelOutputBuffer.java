package com.grendelscan.proxy.ssl;

import java.io.OutputStream;

import org.apache.http.impl.io.AbstractSessionOutputBuffer;
import org.apache.http.params.HttpParams;

import com.grendelscan.logging.Log;

public class SSLTunnelOutputBuffer extends AbstractSessionOutputBuffer
{
	
	public SSLTunnelOutputBuffer(OutputStream sslOutputStream, int buffersize, HttpParams params)
	{
		Log.trace("Creating SSL output buffer");
		init(sslOutputStream, buffersize, params);
	}
}
