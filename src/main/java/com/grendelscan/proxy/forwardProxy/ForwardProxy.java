package com.grendelscan.proxy.forwardProxy;

import java.net.Socket;

import com.grendelscan.proxy.ProxyConfig;
import com.grendelscan.proxy.abstractProxy.AbstractProxy;
import com.grendelscan.proxy.abstractProxy.GenericProxyRequestListenerThread;



public class ForwardProxy extends AbstractProxy
{

	public ForwardProxy(ProxyConfig config)
    {
	    super(config);
    }

	@Override
    public String getName()
    {
	    return "Forward proxy";
    }

	@Override
    public boolean isSSL()
    {
	    return false;
    }
	
	@Override
    public ForwardProxyRequestHandler getRequestHandler(Socket socket, boolean ssl, int sslPort)
	{
		return new ForwardProxyRequestHandler(socket, this, ssl, sslPort);
	}

	@Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
		return new GenericProxyRequestListenerThread(serverSocket, this);
    }

}
