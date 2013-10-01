package com.grendelscan.proxy;

public class ProxyConfig
{
	protected String bindIP;
	protected int bindPort;

	public String getBindIP()
    {
    	return bindIP;
    }
	public void setBindIP(String BindIP)
    {
    	this.bindIP = BindIP;
    }
	public int getBindPort()
    {
    	return bindPort;
    }
	public void setBindPort(int bindPort)
    {
    	this.bindPort = bindPort;
    }

}
