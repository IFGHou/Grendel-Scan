package com.grendelscan.proxy;

import org.apache.http.HttpRequest;

public class ReverseProxyRequestHandler extends AbstractProxyRequestHandler
{
    protected final Destination destination;

    public ReverseProxyRequestHandler(final ReverseProxy proxy)
    {
        super(proxy, proxy.getReverseProxyConfig().isSsl(), proxy.getReverseProxyConfig().getRemotePort());
        destination = new Destination();
        destination.host = proxy.getReverseProxyConfig().getRemoteHost();
        destination.port = proxy.getReverseProxyConfig().getRemotePort();
    }

    @Override
    protected Destination getDestination(final HttpRequest request)
    {
        return destination;
    }

}
