package com.grendelscan.proxy;

import java.util.HashMap;

import com.grendelscan.GUI.MainWindow;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.forwardProxy.ForwardProxy;
import com.grendelscan.proxy.reverseProxy.ReverseProxy;
import com.grendelscan.proxy.reverseProxy.ReverseProxyConfig;
import com.grendelscan.proxy.reverseProxy.SSLReverseProxy;
import com.grendelscan.scan.Scan;

public class Proxies
{
	private final HashMap<ReverseProxyConfig, ReverseProxy> reverseProxies;
	private ForwardProxy forwardProxy;
	
	
	
	public Proxies()
    {
		Log.trace("Instantiating Proxies object");
		reverseProxies = new HashMap<ReverseProxyConfig, ReverseProxy>(1);
    }

	public void startProxies()
	{
		Log.debug("Starting all proxies");
		if (Scan.getScanSettings().isProxyEnabled())
		{
			startForwardProxy();
			if (Scan.getInstance().isGUI())
			{
				MainWindow.getInstance().updateForwardProxyGUIStatus();
			}

		}
		
		for (ReverseProxyConfig reverseConfig: Scan.getScanSettings().getReadOnlyReverseProxyConfigs())
		{
			addReverseProxy(reverseConfig);
			startReverseProxy(reverseConfig);
		}
	}
	
	public void startForwardProxy()
	{
		Log.debug("Starting forward proxy");
		ProxyConfig forwardConfig = new ProxyConfig();
		forwardConfig.setBindPort(Scan.getScanSettings().getProxyPort());
		forwardConfig.setBindIP(Scan.getScanSettings().getProxyIPAddress());
		forwardProxy = new ForwardProxy(forwardConfig);
		forwardProxy.startProxy();
	}

	public void stopForwardProxy()
	{
		Log.debug("Stopping forward proxy");
		if (forwardProxy != null)
			forwardProxy.stopProxy();
		
	}

	
	public void addReverseProxy(ReverseProxyConfig config)
	{
		Log.trace("Adding reverse proxy");
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
	
	public void startReverseProxy(ReverseProxyConfig config)
	{
		Log.debug("Starting reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
		reverseProxies.get(config).startProxy();
	}

	public void stopReverseProxy(ReverseProxyConfig config)
	{
		Log.debug("Stopping reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
		reverseProxies.get(config).stopProxy();
	}

	public boolean isReverseProxyRunning(ReverseProxyConfig config)
	{
		return reverseProxies.get(config).isRunning();
	}
	
	public void stopProxies()
	{
		Log.debug("Stopping all proxies");
		if (forwardProxy != null)
			forwardProxy.stopProxy();
		for(ReverseProxy reverseProxy: reverseProxies.values())
		{
			reverseProxy.stopProxy();
		}
	}
	
//	public HashMap<ReverseProxyConfig, ReverseProxy> getReverseProxies()
//    {
//    	return reverseProxies;
//    }

	public ForwardProxy getForwardProxy()
    {
    	return forwardProxy;
    }
	
	public boolean isRunning()
	{
		boolean running = false;
		if (forwardProxy!= null)
			running = forwardProxy.isRunning();
		
		if (!running)
		{
			for(ReverseProxy reverseProxy: reverseProxies.values())
			{
				if (reverseProxy.isRunning())
				{
					return true;
				}
			}
		}
		return running;
	}

//	public ReverseProxy getReverseProxy(ReverseProxyConfig config)
//    {
//	    return reverseProxies.get(config);
//    }
	
	public void stopAndRemoveReverseProxy(ReverseProxyConfig config)
    {
		stopReverseProxy(config);
		Log.debug("Removing reverse proxy for " + config.getRemoteHost() + ":" + config.getRemotePort());
	    reverseProxies.remove(config);
    }
	
}
