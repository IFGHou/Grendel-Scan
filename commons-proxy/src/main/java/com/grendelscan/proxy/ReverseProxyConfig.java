package com.grendelscan.proxy;

public class ReverseProxyConfig extends ProxyConfig
{
    private String webHostname;
    private String remoteHost;
    private int remotePort;
    private boolean ssl;

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public int getRemotePort()
    {
        return remotePort;
    }

    public String getWebHostname()
    {
        return webHostname;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public void setRemoteHost(final String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(final int remotePort)
    {
        this.remotePort = remotePort;
    }

    public void setSsl(final boolean ssl)
    {
        this.ssl = ssl;
    }

    public void setWebHostname(final String webHostname)
    {
        this.webHostname = webHostname;
    }
}
