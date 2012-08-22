package com.grendelscan.proxy.reverseProxy;


import java.net.Socket;

import com.grendelscan.proxy.abstractProxy.AbstractProxy;
import com.grendelscan.proxy.abstractProxy.AbstractProxyRequestHandler;
import com.grendelscan.proxy.abstractProxy.GenericProxyRequestListenerThread;

public class ReverseProxy extends AbstractProxy
{
	private final ReverseProxyConfig reverseConfig;
	public ReverseProxy(ReverseProxyConfig config)
    {
	    super(config);
	    reverseConfig = config;
    }

	@Override
    public String getName()
    {
	    return "Reverse proxy for " + reverseConfig.getWebHostname() + " -> " +
	    	reverseConfig.getRemoteHost() + ":" + reverseConfig.getRemotePort() + " on " + 
	    	reverseConfig.getBindIP() + ":" + reverseConfig.getBindPort();
    }

	public ReverseProxyConfig getReverseProxyConfig()
    {
    	return reverseConfig;
    }

	@Override
    public boolean isSSL()
    {
	    return false;
    }

	@Override
    public AbstractProxyRequestHandler getRequestHandler(Socket socket, boolean ssl, int sslPort)
    {
	    return new ReverseProxyRequestHandler(this);
    }

	@Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
		return new ReverseProxyRequestListenerThread(serverSocket, this);
    }
}
