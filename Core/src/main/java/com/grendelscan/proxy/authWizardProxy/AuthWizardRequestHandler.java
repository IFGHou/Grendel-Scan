package com.grendelscan.proxy.authWizardProxy;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;

import com.grendelscan.GUI.AuthWizard.AuthWizardDialog;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.forwardProxy.ForwardProxyRequestHandler;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.StringUtils;

public class AuthWizardRequestHandler extends ForwardProxyRequestHandler
{
	protected AuthWizardDialog wizardDialog;
	protected AuthWizardProxy wizardProxy;
	
	public AuthWizardRequestHandler(Socket socket, AuthWizardProxy wizardProxy, boolean ssl, int sslPort, AuthWizardDialog wizardDialog)
    {
		super(socket, wizardProxy, ssl, sslPort);
	    this.wizardDialog = wizardDialog;
	    this.wizardProxy = wizardProxy;
    }

//	@Override
//    protected HttpService makeHttpServiceProxy(Socket socket, ForwardProxy proxy, CustomHttpClient proxyHttpClient,
//            boolean ssl, int sslPort)
//    {
//		HttpParams params = MiscHttpFactory.createDefaultHttpParams();
//		BasicHttpProcessor httpproc = new BasicHttpProcessor();
//		httpproc.addInterceptor(new ResponseConnControl());
//		
//		// Set up request handlers
//		HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
//		reqistry.register("*", new AuthWizardRequestHandler(scan, socket, proxy, proxyHttpClient, ssl, sslPort, wizardDialog, wizardProxy));
//		
//		// Set up the HTTP service
//		HttpService httpService =
//		        new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
//		httpService.setParams(params);
//		httpService.setHandlerResolver(reqistry);
//		
//		return httpService;
//    }

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws IOException
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
					Log.error(e.toString(), e);
					throw e;
				}
			}
			wizardDialog.requestComplete();
		}
	}

	
}
