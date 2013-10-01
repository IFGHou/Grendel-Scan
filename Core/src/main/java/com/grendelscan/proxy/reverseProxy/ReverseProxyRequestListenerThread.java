package com.grendelscan.proxy.reverseProxy;

import java.net.ServerSocket;

import com.grendelscan.proxy.abstractProxy.GenericProxyRequestListenerThread;

public class ReverseProxyRequestListenerThread extends GenericProxyRequestListenerThread
{
	protected final ReverseProxy reverseProxy;
	
	public ReverseProxyRequestListenerThread(ServerSocket serverSocket, ReverseProxy proxy)
	{
		super(serverSocket, proxy);
		this.reverseProxy = proxy;
	}
}
