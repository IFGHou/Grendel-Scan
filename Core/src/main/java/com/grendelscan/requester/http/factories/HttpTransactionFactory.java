//package com.grendelscan.requester.http.factories;
//
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import org.apache.http.HttpRequest;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.message.BasicHttpEntityEnclosingRequest;
//import org.apache.http.message.BasicHttpRequest;
//
//import com.grendelscan.requester.TransactionSource;
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
//import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
//import com.grendelscan.requester.http.transactions.HttpPostTransaction;
//import com.grendelscan.utils.Log;
//
//public class HttpTransactionFactory
//{
//	/**
//	 * 
//	 * @param sharedComponents
//	 * @param method
//	 * @param uri
//	 * @param referer
//	 * @param source
//	 * @return
//	 * @throws URISyntaxException
//	 */
//	public static StandardHttpTransaction createTransaction(String method, String uri, TransactionSource source) throws URISyntaxException
//	{
//		URI u = UriFactory.makeUri(uri, true);
//		return createTransaction(method, u, source);
//	}
//	
//	/**
//	 * 
//	 * @param scan
//	 * @param method
//	 * @param uri
//	 * @param referer
//	 * @param source
//	 * @return
//	 */
//	public static StandardHttpTransaction createTransaction(String method, URI uri, TransactionSource source) throws URISyntaxException
//	{
//		StandardHttpTransaction transaction = null;
//		
//		if (method.equalsIgnoreCase("get"))
//		{
//			transaction = new StandardHttpTransaction(uri, source);
//		}
//		else if (method.equalsIgnoreCase("post"))
//		{
//			transaction = new HttpPostTransaction(uri, source);
//		}
//		
//		return transaction;
//	}
//	
//	public static HttpGet convertGenericGet(HttpRequest request)
//	{
//		HttpGet get = null;
//		get = new HttpGet(request.getRequestLine().getUri());
//		get.setHeaders(request.getAllHeaders());
//		get.setParams(request.getParams());
//		return get;
//	}
//	
//	
//	public static HttpPost convertGenericPost(BasicHttpEntityEnclosingRequest request)
//	{
//		HttpPost post = null;
//		post = new HttpPost(request.getRequestLine().getUri());
//		post.setHeaders(request.getAllHeaders());
//		post.setEntity(request.getEntity());
//		post.setParams(request.getParams());
//		return post;
//	}
//	
//	
//	public static StandardHttpTransaction createTransaction(HttpRequest request, TransactionSource source)
//	{
//		return createTransaction(request, source, false, 0);
//	}
//	
//	public static StandardHttpTransaction createTransaction(HttpRequest request, TransactionSource source, boolean ssl, int sslPort)
//	{
//		StandardHttpTransaction transaction = null;
//		if (request.getRequestLine().getMethod().equals("GET"))
//		{
//			HttpGet get = null;
//			if (request instanceof BasicHttpRequest)
//			{
//				get = convertGenericGet(request);
//			}
//			else if (request instanceof HttpGet)
//			{
//				get = (HttpGet) request;
//			}
//			
//			try
//            {
//	            transaction = new StandardHttpTransaction(get, source);
//            }
//            catch (URISyntaxException e)
//            {
//	            Log.error("Problem creating transaction due to uri: " + e.toString(), e);
//            }
//		}
//		else if (request.getRequestLine().getMethod().equals("POST"))
//		{
//			HttpPost post = null;
//			if (request instanceof BasicHttpEntityEnclosingRequest)
//			{
//				post = convertGenericPost((BasicHttpEntityEnclosingRequest) request);
//			}
//			else if (request instanceof HttpPost)
//			{
//				post = (HttpPost) request;
//			}
//			
//			try
//            {
//	            transaction = new HttpPostTransaction(post, source);
//            }
//            catch (URISyntaxException e)
//            {
//	            Log.error("Problem creating transaction due to uri: " + e.toString(), e);
//            }
//		}
//		return transaction;
//	}
//}
