package com.grendelscan.ui.AuthWizard;

import java.net.Socket;

import com.grendelscan.proxy.ForwardProxy;
import com.grendelscan.proxy.ForwardProxyRequestHandler;
import com.grendelscan.proxy.ProxyConfig;

public class AuthWizardProxy extends ForwardProxy
{

    AuthWizardDialog wizardDialog;

    public AuthWizardProxy(final AuthWizardDialog wizardDialog, final ProxyConfig config) throws IllegalStateException
    {
        super(config);
        this.wizardDialog = wizardDialog;
        startProxy();
        if (!isRunning())
        {
            throw new IllegalStateException("Proxy failed to start");
        }
    }

    @Override
    public ForwardProxyRequestHandler getRequestHandler(final Socket socket, final boolean ssl, final int sslPort)
    {
        return new AuthWizardRequestHandler(socket, this, ssl, sslPort, wizardDialog);
    }

}
