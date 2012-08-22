package com.grendelscan.proxy.reverseProxy;


import org.apache.http.HttpRequest;

import com.grendelscan.proxy.Destination;
import com.grendelscan.proxy.abstractProxy.AbstractProxyRequestHandler;

public class ReverseProxyRequestHandler extends AbstractProxyRequestHandler
{
	protected final Destination destination;
	
	public ReverseProxyRequestHandler(ReverseProxy proxy)
	{
		super(proxy, proxy.getReverseProxyConfig().isSsl(), proxy.getReverseProxyConfig().getRemotePort());
		destination = new Destination();
		destination.host = proxy.getReverseProxyConfig().getRemoteHost();
		destination.port = proxy.getReverseProxyConfig().getRemotePort();
	}
	
	@Override
	protected Destination getDestination(HttpRequest request)
	{
		return destination;
	}
	
}
