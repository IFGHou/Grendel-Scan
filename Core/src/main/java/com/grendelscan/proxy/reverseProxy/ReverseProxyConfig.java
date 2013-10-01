package com.grendelscan.proxy.reverseProxy;

import com.grendelscan.proxy.ProxyConfig;

public class ReverseProxyConfig extends ProxyConfig
{
	private String webHostname;
	private String remoteHost;
	private int remotePort;
	private boolean ssl;
	
	
	public String getWebHostname()
    {
    	return webHostname;
    }
	public void setWebHostname(String webHostname)
    {
    	this.webHostname = webHostname;
    }
	public String getRemoteHost()
    {
    	return remoteHost;
    }
	public void setRemoteHost(String remoteHost)
    {
    	this.remoteHost = remoteHost;
    }
	public int getRemotePort()
    {
    	return remotePort;
    }
	public void setRemotePort(int remotePort)
    {
    	this.remotePort = remotePort;
    }
	public boolean isSsl()
    {
    	return ssl;
    }
	public void setSsl(boolean ssl)
    {
    	this.ssl = ssl;
    }
}
