package com.grendelscan.proxy;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Proxies
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Proxies.class);
    private final HashMap<ReverseProxyConfig, ReverseProxy> reverseProxies;
    private ForwardProxy forwardProxy;

    public Proxies()
    {
        LOGGER.trace("Instantiating Proxies object");
        reverseProxies = new HashMap<ReverseProxyConfig, ReverseProxy>(1);
    }

    public void addReverseProxy(final ReverseProxyConfig config)
    {
        LOGGER.trace("Adding reverse proxy");
        ReverseProxy proxy;
        if (config.isSsl())
        {
            proxy = new SSLReverseProxy(config);
        }
        else
        {
            proxy = new ReverseProxy(config);
        }
        reverseProxies.put(config, proxy);
    }

    public ForwardProxy getForwardProxy()
    {
        return forwardProxy;
    }

    public boolean isReverseProxyRunning(final ReverseProxyConfig config)
    {
        return reverseProxies.get(config).isRunning();
    }

    public boolean isRunning()
    {
        boolean running = false;
        if (forwardProxy != null)
        {
            running = forwardProxy.isRunning();
        }

        if (!running)
        {
            for (ReverseProxy reverseProxy : reverseProxies.values())
            {
                if (reverseProxy.isRunning())
                {
                    return true;
                }
            }
        }
        return running;
    }

    public void startForwardProxy()
    {
        LOGGER.debug("Starting forward proxy");
        ProxyConfig forwardConfig = new ProxyConfig();
        forwardConfig.setBindPort(Scan.getScanSettings().getProxyPort());
        forwardConfig.setBindIP(Scan.getScanSettings().getProxyIPAddress());
        forwardProxy = new ForwardProxy(forwardConfig);
        forwardProxy.startProxy();
    }

    public void startProxies()
    {
        LOGGER.debug("Starting all proxies");
        if (Scan.getScanSettings().isProxyEnabled())
        {
            startForwardProxy();
            if (Scan.getInstance().isGUI())
            {
                MainWindow.getInstance().updateForwardProxyGUIStatus();
            }

        }

        for (ReverseProxyConfig reverseConfig : Scan.getScanSettings().getReadOnlyReverseProxyConfigs())
        {
            addReverseProxy(reverseConfig);
            startReverseProxy(reverseConfig);
        }
    }

    public void startReverseProxy(final ReverseProxyConfig config)
    {
        LOGGER.debug("Starting reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
        reverseProxies.get(config).startProxy();
    }

    public void stopAndRemoveReverseProxy(final ReverseProxyConfig config)
    {
        stopReverseProxy(config);
        LOGGER.debug("Removing reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
        reverseProxies.remove(config);
    }

    // public HashMap<ReverseProxyConfig, ReverseProxy> getReverseProxies()
    // {
    // return reverseProxies;
    // }

    public void stopForwardProxy()
    {
        LOGGER.debug("Stopping forward proxy");
        if (forwardProxy != null)
        {
            forwardProxy.stopProxy();
        }

    }

    public void stopProxies()
    {
        LOGGER.debug("Stopping all proxies");
        if (forwardProxy != null)
        {
            forwardProxy.stopProxy();
        }
        for (ReverseProxy reverseProxy : reverseProxies.values())
        {
            reverseProxy.stopProxy();
        }
    }

    // public ReverseProxy getReverseProxy(ReverseProxyConfig config)
    // {
    // return reverseProxies.get(config);
    // }

    public void stopReverseProxy(final ReverseProxyConfig config)
    {
        LOGGER.debug("Stopping reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
        reverseProxies.get(config).stopProxy();
    }

}
