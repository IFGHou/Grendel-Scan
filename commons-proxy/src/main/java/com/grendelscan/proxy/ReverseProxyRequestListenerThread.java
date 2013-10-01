package com.grendelscan.proxy;

import java.net.ServerSocket;

public class ReverseProxyRequestListenerThread extends GenericProxyRequestListenerThread
{
    protected final ReverseProxy reverseProxy;

    public ReverseProxyRequestListenerThread(final ServerSocket serverSocket, final ReverseProxy proxy)
    {
        super(serverSocket, proxy);
        reverseProxy = proxy;
    }
}
