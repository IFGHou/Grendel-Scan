package com.grendelscan.proxy;

import java.net.Socket;

public class ReverseProxy extends AbstractProxy
{
    private final ReverseProxyConfig reverseConfig;

    public ReverseProxy(final ReverseProxyConfig config)
    {
        super(config);
        reverseConfig = config;
    }

    @Override
    protected GenericProxyRequestListenerThread createRequestListenerThread()
    {
        return new ReverseProxyRequestListenerThread(serverSocket, this);
    }

    @Override
    public String getName()
    {
        return "Reverse proxy for " + reverseConfig.getWebHostname() + " -> " + reverseConfig.getRemoteHost() + ":" + reverseConfig.getRemotePort() + " on " + reverseConfig.getBindIP() + ":" + reverseConfig.getBindPort();
    }

    @Override
    public AbstractProxyRequestHandler getRequestHandler(final Socket socket, final boolean ssl, final int sslPort)
    {
        return new ReverseProxyRequestHandler(this);
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
}
