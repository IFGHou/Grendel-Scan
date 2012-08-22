package com.grendelscan.proxy.authWizardProxy;


import java.net.Socket;

import com.grendelscan.GUI.AuthWizard.AuthWizardDialog;
import com.grendelscan.proxy.ProxyConfig;
import com.grendelscan.proxy.forwardProxy.ForwardProxy;
import com.grendelscan.proxy.forwardProxy.ForwardProxyRequestHandler;

public class AuthWizardProxy extends ForwardProxy
{
	
	AuthWizardDialog wizardDialog;
	public AuthWizardProxy(AuthWizardDialog wizardDialog, ProxyConfig config) throws IllegalStateException
	{
		super(config);
		this.wizardDialog = wizardDialog;
		startProxy();
		if (!this.isRunning())
		{
			throw new IllegalStateException("Proxy failed to start");
		}
	}
	
	@Override
    public ForwardProxyRequestHandler getRequestHandler(Socket socket, boolean ssl, int sslPort)
    {
		return new AuthWizardRequestHandler(socket, this, ssl, sslPort, wizardDialog);
    }


	
}
