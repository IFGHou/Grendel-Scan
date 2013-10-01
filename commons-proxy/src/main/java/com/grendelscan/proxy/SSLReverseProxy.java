package com.grendelscan.proxy;

public class SSLReverseProxy extends ReverseProxy
{

    public SSLReverseProxy(final ReverseProxyConfig config)
    {
        super(config);
    }

    @Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
        return new SSLReverseProxyRequestListenerThread(serverSocket, this);
    }

    @Override
    public boolean isSSL()
    {
        return true;
    }

}
