package com.grendelscan.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class HttpCloner
{
	/**
	 * This will always return a BasicHeader. If you need a BufferedHeader, do it yourself
	 * @param header
	 * @return
	 */
	public static Header clone(Header header)
	{
		return new BasicHeader(header.getName(), header.getValue());
	}
	
	/**
	 * This isn't perfect. Some fields can't be obtained, such as releaseTrigger. 
	 * HttpParams is reused not cloned 
	 * @param get
	 * @return
	 */
	public static HttpGet clone(HttpGet get)
	{
		HttpGet newGet = new HttpGet(get.getURI());
		for (Header header: get.getAllHeaders())
		{
			newGet.addHeader(HttpCloner.clone(header));
		}
		
		newGet.setParams(get.getParams());
		
		
		return newGet;
	}

//	/**
//	 * This isn't perfect. Some fields can't be obtained, such as releaseTrigger. 
//	 * HttpParams is reused, not cloned.
//	 * @param request
//	 * @return
//	 */
//	public static HttpRequest clone(HttpUriRequest request, byte[] requestBody)
//	{
//		HttpRequest clone = HttpRequestFactory.makeRequest(request.getMethod(), URI.create(request.getURI().toASCIIString()), request.getParams(), null, null);
//		for (Header header: request.getAllHeaders())
//		{
//			clone.addHeader(HttpCloner.clone(header));
//		}
//		if (request instanceof HttpEntityEnclosingRequest)
//		{
//			
//		}
//		return null;
//		HttpUriRequest clone = HttpRequestFactory.
//		HttpGet newGet = new HttpGet(get.getURI());
//		for (Header header: get.getAllHeaders())
//		{
//			newGet.addHeader(HttpCloner.clone(header));
//		}
//		
//		newGet.setParams(get.getParams());
//		
//		
//		return newGet;
//	}

	/** 
	 * This cannot properly clone the entity body. If the entity is 
	 * repeatable, it will be reused (not copied). Otherwise, you get
	 * a blank entity. I hope to improve/fix this some day.
	 *  
	 * @param post
	 * @return
	 */
	public static HttpPost clone(HttpPost post, HttpEntity entity)
	{
		HttpPost clone = new HttpPost(post.getURI());
		for (Header header: post.getAllHeaders())
		{
			clone.addHeader(HttpCloner.clone(header));
		}
		clone.setParams(post.getParams());
		clone.setEntity(entity);
		
		return clone;
	}

	public static StatusLine clone(StatusLine statusLine)
	{
		StatusLine newStatusLine = new BasicStatusLine(clone(statusLine.getProtocolVersion()), statusLine.getStatusCode(), statusLine.getReasonPhrase());
		return newStatusLine;
	}
	
	public static ProtocolVersion clone(ProtocolVersion version)
	{
		ProtocolVersion newVersion = new ProtocolVersion(version.getProtocol(), version.getMajor(), version.getMinor());
		return newVersion;
	}
	
	
	public static HttpResponse clone(HttpResponse response, byte responseBody[])
	{
		HttpResponse clone = new BasicHttpResponse(clone(response.getStatusLine()));
		clone.setParams(response.getParams());
		if (responseBody != null)
		{
			ByteArrayEntity entity = new ByteArrayEntity(responseBody);
			clone.setEntity(entity);
		}
		for (Header header: response.getAllHeaders())
		{
			clone.addHeader(clone(header));
		}
		return clone;
	}
}
