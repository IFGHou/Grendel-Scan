package com.grendelscan.proxy;

import java.net.Socket;

public class ForwardProxy extends AbstractProxy
{

    public ForwardProxy(final ProxyConfig config)
    {
        super(config);
    }

    @Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
        return new GenericProxyRequestListenerThread(serverSocket, this);
    }

    @Override
    public String getName()
    {
        return "Forward proxy";
    }

    @Override
    public ForwardProxyRequestHandler getRequestHandler(final Socket socket, final boolean ssl, final int sslPort)
    {
        return new ForwardProxyRequestHandler(socket, this, ssl, sslPort);
    }

    @Override
    public boolean isSSL()
    {
        return false;
    }

}
