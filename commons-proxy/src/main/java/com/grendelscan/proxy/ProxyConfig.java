package com.grendelscan.proxy;

public class ProxyConfig
{
    protected String bindIP;
    protected int bindPort;

    public String getBindIP()
    {
        return bindIP;
    }

    public int getBindPort()
    {
        return bindPort;
    }

    public void setBindIP(final String BindIP)
    {
        bindIP = BindIP;
    }

    public void setBindPort(final int bindPort)
    {
        this.bindPort = bindPort;
    }

}
