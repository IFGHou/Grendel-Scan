package com.grendelscan.ui.AuthWizard;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.proxy.ForwardProxyRequestHandler;

public class AuthWizardRequestHandler extends ForwardProxyRequestHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthWizardRequestHandler.class);
    protected AuthWizardDialog wizardDialog;
    protected AuthWizardProxy wizardProxy;

    public AuthWizardRequestHandler(final Socket socket, final AuthWizardProxy wizardProxy, final boolean ssl, final int sslPort, final AuthWizardDialog wizardDialog)
    {
        super(socket, wizardProxy, ssl, sslPort);
        this.wizardDialog = wizardDialog;
        this.wizardProxy = wizardProxy;
    }

    // @Override
    // protected HttpService makeHttpServiceProxy(Socket socket, ForwardProxy proxy, CustomHttpClient proxyHttpClient,
    // boolean ssl, int sslPort)
    // {
    // HttpParams params = MiscHttpFactory.createDefaultHttpParams();
    // BasicHttpProcessor httpproc = new BasicHttpProcessor();
    // httpproc.addInterceptor(new ResponseConnControl());
    //
    // // Set up request handlers
    // HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
    // reqistry.register("*", new AuthWizardRequestHandler(scan, socket, proxy, proxyHttpClient, ssl, sslPort, wizardDialog, wizardProxy));
    //
    // // Set up the HTTP service
    // HttpService httpService =
    // new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
    // httpService.setParams(params);
    // httpService.setHandlerResolver(reqistry);
    //
    // return httpService;
    // }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws IOException
    {
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT"))
        {
            handleConnectMethod(request, response, context);
        }
        else
        {
            wizardProxy.stopProxy();
            wizardDialog.setHttpMethod(method);
            wizardDialog.setUri(request.getRequestLine().getUri());
            if (method.equalsIgnoreCase("POST"))
            {
                if (request instanceof BasicHttpEntityEnclosingRequest)
                {
                    HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
                    String postQuery = new String(HttpUtils.entityToByteArray(entity, 0), StringUtils.getDefaultCharset());
                    wizardDialog.setPostQuery(postQuery);
                }
                else
                {
                    IllegalStateException e = new IllegalStateException("Problem with post in wizard");
                    LOGGER.error(e.toString(), e);
                    throw e;
                }
            }
            wizardDialog.requestComplete();
        }
    }

}
