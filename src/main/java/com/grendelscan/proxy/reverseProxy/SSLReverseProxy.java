package com.grendelscan.proxy.reverseProxy;

import com.grendelscan.proxy.abstractProxy.GenericProxyRequestListenerThread;

public class SSLReverseProxy extends ReverseProxy
{
	
	public SSLReverseProxy(ReverseProxyConfig config)
	{
		super(config);
	}
	
	@Override
    public boolean isSSL()
    {
	    return true;
    }

	@Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
		return new SSLReverseProxyRequestListenerThread(serverSocket, this);
    }

}
