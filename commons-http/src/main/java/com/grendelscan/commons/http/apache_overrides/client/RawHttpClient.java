package com.grendelscan.commons.http.apache_overrides.client;
//package com.grendelscan.commons.http.client;
//
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.BasicHttpProcessor;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.protocol.RequestExpectContinue;
//import org.apache.http.protocol.RequestUserAgent;
//
//public class RawHttpClient extends CustomHttpClient
//{
//	
//	public RawHttpClient(ClientConnectionManager conman, HttpParams params, HttpContext contextTemplate)
//	{
//		super(conman, params, contextTemplate);
//	}
//	
////	@Override
////    protected RequestDirector createClientRequestDirector(
////            final HttpRequestExecutor requestExec,
////            final ClientConnectionManager conman,
////            final ConnectionReuseStrategy reustrat,
////            final ConnectionKeepAliveStrategy kastrat,
////            final HttpRoutePlanner rouplan,
////            final HttpProcessor httpProcessor,
////            final HttpRequestRetryHandler retryHandler,
////            final RedirectHandler redirectHandler,
////            final AuthenticationHandler targetAuthHandler,
////            final AuthenticationHandler proxyAuthHandler,
////            final UserTokenHandler stateHandler,
////            final HttpParams params)
////	{
////		return new RawClientRequestDirector(conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectHandler,
////		        targetAuthHandler, proxyAuthHandler, stateHandler, params);
////	}
//	
//	@Override
//	protected BasicHttpProcessor createHttpProcessor()
//	{
//		BasicHttpProcessor httpproc = new BasicHttpProcessor();
//		httpproc.addInterceptor(new RequestUserAgent());
//		httpproc.addInterceptor(new RequestExpectContinue());
//		return httpproc;
//	}
//	
//}
