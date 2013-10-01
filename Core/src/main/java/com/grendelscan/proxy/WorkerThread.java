//package com.grendelscan.proxy;
//
//
//import java.io.IOException;
//
//import org.apache.http.ConnectionClosedException;
//import org.apache.http.HttpException;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.protocol.HttpService;
//
//import com.grendelscan.utils.Debug;
//
//public class WorkerThread extends Thread
//{
//
//	private final DefaultHttpProxyConnection httpProxyConnection;
//	private final HttpService httpService;
//	private AbstractProxy proxy;
//
//	public WorkerThread(final HttpService httpService, final DefaultHttpProxyConnection conn, AbstractProxy proxy)
//	{
//		this.proxy = proxy;
//		this.httpService = httpService;
//		this.httpProxyConnection = conn;
//	}
//
//	@Override
//	public void run()
//	{
//		Debug.debug("New proxy connection thread");
//		HttpContext context = new BasicHttpContext(null);
//		try
//		{
//			while (!Thread.interrupted() && httpProxyConnection.isOpen() && proxy.isRunning())
//			{
//				httpService.handleRequest(httpProxyConnection, context);
//			}
//		}
//		catch (ConnectionClosedException ex)
//		{
////			Debug.errDebug("Client closed connection", ex);
//		}
//		catch (IOException ex)
//		{
////			Debug.errDebug("I/O error: " + ex.getMessage(), ex);
//		}
//		catch (HttpException ex)
//		{
//			Debug.errDebug("Unrecoverable HTTP protocol violation: " + ex.getMessage(), ex);
//		}
//		{
//			Debug.errDebug("Big problem in proxy WorkerThread: " + e.toString(), e);
//		}
//		finally
//		{
//			try
//			{
//				httpProxyConnection.shutdown();
//			}
//			catch (IOException ignore)
//			{
//			}
//		}
//	}
//
//}
