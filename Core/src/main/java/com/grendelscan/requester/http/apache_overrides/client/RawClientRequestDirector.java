package com.grendelscan.requester.http.apache_overrides.client;
//package com.grendelscan.requester.http.client;
//
//import java.io.IOException;
//import java.io.InterruptedIOException;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.http.ConnectionReuseStrategy;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpException;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.AuthenticationHandler;
//import org.apache.http.client.HttpRequestRetryHandler;
//import org.apache.http.client.RedirectHandler;
//import org.apache.http.client.UserTokenHandler;
//import org.apache.http.client.methods.AbortableHttpRequest;
//import org.apache.http.conn.BasicManagedEntity;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.ClientConnectionRequest;
//import org.apache.http.conn.ConnectionKeepAliveStrategy;
//import org.apache.http.conn.ManagedClientConnection;
//import org.apache.http.conn.params.ConnManagerParams;
//import org.apache.http.conn.routing.HttpRoute;
//import org.apache.http.conn.routing.HttpRoutePlanner;
//import org.apache.http.impl.client.DefaultRequestDirector;
//import org.apache.http.impl.client.TunnelRefusedException;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.ExecutionContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.protocol.HttpProcessor;
//
//public class RawClientRequestDirector extends DefaultRequestDirector
//{
//	
//	public RawClientRequestDirector(
//			ClientConnectionManager conman, 
//			ConnectionReuseStrategy reustrat,
//			ConnectionKeepAliveStrategy kastrat,
//	        HttpRoutePlanner rouplan, 
//	        HttpProcessor httpProcessor, 
//	        HttpRequestRetryHandler retryHandler,
//	        RedirectHandler redirectHandler, 
//	        AuthenticationHandler targetAuthHandler,
//	        AuthenticationHandler proxyAuthHandler, 
//	        UserTokenHandler userTokenHandler,
//	        HttpParams params)
//	        {
//		super(new CustomHttpRequestExecutor(), conman, reustrat, kastrat, rouplan, 
//				httpProcessor, retryHandler, redirectHandler, targetAuthHandler,
//		        proxyAuthHandler, userTokenHandler, params);
//	}
//	
////	@Override
////	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException,
////	        IOException
////	{
////		HttpRequest orig = request;
////		// RequestWrapper origWrapper = wrapRequest(orig);
////		orig.setParams(params);
////		HttpRoute origRoute = determineRoute(target, orig, context);
////		
////		// RoutedRequest roureq = new RoutedRequest(origWrapper, origRoute);
////		
////		long timeout = ConnManagerParams.getTimeout(params);
////		
////		int execCount = 0;
////		
////		HttpResponse response = null;
////		boolean done = false;
////		try
////		{
////			while (!done)
////			{
////				// In this loop, the RoutedRequest may be replaced by a
////				// followup request and route. The request and route passed
////				// in the method arguments will be replaced. The original
////				// request is still available in 'orig'.
////				
////				// RequestWrapper wrapper = roureq.getRequest();
////				// HttpRoute route = roureq.getRoute();
////				
////				// Allocate connection if needed
////				if (managedConn == null)
////				{
////					ClientConnectionRequest connRequest = connManager.requestConnection(origRoute, null);
////					if (orig instanceof AbortableHttpRequest)
////					{
////						((AbortableHttpRequest) orig).setConnectionRequest(connRequest);
////					}
////					
////					try
////					{
////						managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
////					}
////					catch (InterruptedException interrupted)
////					{
////						InterruptedIOException iox = new InterruptedIOException();
////						iox.initCause(interrupted);
////						throw iox;
////					}
////
////				
////                    if (HttpConnectionParams.isStaleCheckingEnabled(params)) 
////                    {
////                        // validate connection
////                        if (managedConn.isOpen()) 
////                        {
//////                            this.log.debug("Stale connection check");
////                            if (managedConn.isStale()) 
////                            {
//////                                this.log.debug("Stale connection detected");
////                                managedConn.close();
////                            }
////                        }
////                    }
////
////				}
////				
////				if (orig instanceof AbortableHttpRequest)
////				{
////					((AbortableHttpRequest) orig).setReleaseTrigger(managedConn);
////				}
////				
////				// Reopen connection if needed
////				if (!managedConn.isOpen())
////				{
////					managedConn.open(origRoute, context, params);
////				}
////				
////				try
////				{
////					establishRoute(origRoute, context);
////				}
////				catch (TunnelRefusedException ex)
////				{
////					response = ex.getResponse();
////					break;
////				}
////				
//////				if (HttpConnectionParams.isStaleCheckingEnabled(params))
//////				{
//////					// validate connection
//////					if (managedConn.isStale())
//////					{
//////						managedConn.close();
//////						continue;
//////					}
//////				}
////				
////				target = origRoute.getTargetHost();
////				
////				HttpHost proxy = origRoute.getProxyHost();
////				
////				// Populate the execution context
////				context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);
////				context.setAttribute(ExecutionContext.HTTP_PROXY_HOST, proxy);
////				context.setAttribute(ExecutionContext.HTTP_CONNECTION, managedConn);
////
//////                context.setAttribute(ClientContext.TARGET_AUTH_STATE,
//////                        targetAuthState);
//////                context.setAttribute(ClientContext.PROXY_AUTH_STATE,
//////                        proxyAuthState);
////
////				context.setAttribute(ExecutionContext.HTTP_REQUEST, request);
////				requestExec.preProcess(request, httpProcessor, context);
////				
////				
////				execCount++;
////				try
////				{
////					response = requestExec.execute(request, managedConn, context);
////					
////				}
////				catch (IOException ex)
////				{
////					managedConn.close();
////					if (retryHandler.retryRequest(ex, execCount, context))
////					{
////						continue;
////					}
////					throw ex;
////				}
////				
////				response.setParams(params);
////				requestExec.postProcess(response, httpProcessor, context);
////				
////				// RoutedRequest followup = handleResponse(roureq, response,
////				// context);
////				done = true;
////			} // while not done
////			
////			// The connection is in or can be brought to a re-usable state.
////			boolean reuse = reuseStrategy.keepAlive(response, context);
////			
////            if (reuse) 
////            {
////                // Set the idle duration of this connection
////                long duration = keepAliveStrategy.getKeepAliveDuration(response, context);
////                managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
////
//////                if (this.log.isDebugEnabled()) {
//////                    this.log.debug("Connection can be kept alive for " + duration + " ms");
//////                }
////            }
////
////			
////			// check for entity, release connection if possible
////			if ((response == null) || (response.getEntity() == null) || !response.getEntity().isStreaming())
////			{
////				// connection not needed and (assumed to be) in re-usable state
////				if (reuse)
////				{
////					managedConn.markReusable();
////				}
////				managedConn.releaseConnection();
////				managedConn = null;
////			}
////			else
////			{
////				// install an auto-release entity
////				HttpEntity entity = response.getEntity();
////				entity = new BasicManagedEntity(entity, managedConn, reuse);
////				response.setEntity(entity);
////			}
////			
////			return response;
////			
////		}
////		catch (HttpException ex)
////		{
////			abortConnection();
////			throw ex;
////		}
////		catch (IOException ex)
////		{
////			abortConnection();
////			throw ex;
////		}
////		catch (RuntimeException ex)
////		{
////			abortConnection();
////			throw ex;
////		}
////	}
////	
////    private void abortConnection() 
////    {
////        ManagedClientConnection mcc = managedConn;
////        if (mcc != null) 
////        {
////            // we got here as the result of an exception
////            // no response will be returned, release the connection
////            managedConn = null;
////            try 
////            {
////                mcc.abortConnection();
////            } 
////            catch (IOException ex) 
////            {
//////                if (this.log.isDebugEnabled()) 
//////                {
//////                    this.log.debug(ex.getMessage(), ex);
//////                }
////            }
////            // ensure the connection manager properly releases this connection
////            try 
////            {
////                mcc.releaseConnection();
////            } 
////            catch(IOException ignored) 
////            {
//////                this.log.debug("Error releasing connection", ignored);
////            }
////        }
////    } // abortConnection
//
////	private void abortConnection() throws IOException
////	{
////		ManagedClientConnection mcc = managedConn;
////		if (mcc != null)
////		{
////			// we got here as the result of an exception
////			// no response will be returned, release the connection
////			managedConn = null;
////			try
////			{
////				mcc.abortConnection();
////			}
////			catch (IOException ex)
////			{
////			}
////		}
////		// ensure the connection manager properly releases this connection
////		mcc.releaseConnection();
////	}
////	// abortConnection
//	
//}
