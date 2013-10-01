package com.grendelscan.commons.http;
//package com.grendelscan.commons.http;
//
//import java.io.IOException;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpEntityEnclosingRequest;
//import org.apache.http.HttpHost;
//import org.apache.http.entity.ByteArrayEntity;
//
//import com.grendelscan.commons.ArrayUtils;
//import com.grendelscan.commons.http.HttpUtils;
//import com.grendelscan.commons.Log;
//
//
//public class UnvalidatedHttpEntityRequest extends UnvalidatedHttpRequest implements HttpEntityEnclosingRequest
//{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -3768084680860166736L;
//	private byte body[];
//	
//	public UnvalidatedHttpEntityRequest(String method, String uri, String protocol, int majorProtocolVersion, int minorProtocolVersion,
//	        String host, int port, boolean ssl, byte[] body)
//	{
//		super(method, uri, protocol, majorProtocolVersion, minorProtocolVersion, host, port, ssl);
//		this.body = body;
//	}
//	
//	public UnvalidatedHttpEntityRequest(String method, String uri, String host, int port, boolean ssl, byte[] body)
//	{
//		super(method, uri, host, port, ssl);
//		this.body = body;
//	}
//	
//	private UnvalidatedHttpEntityRequest()
//	{
//	}
//	
//	public UnvalidatedHttpRequest clone()
//	{
//		UnvalidatedHttpEntityRequest clone = new UnvalidatedHttpEntityRequest();
//		clone.headergroup = headergroup.clone();
//		clone.requestLine = requestLine.clone();
//		clone.targetedHost = targetedHost;
//		clone.scheme = scheme;
//		clone.targetedPort = targetedPort;
//		clone.target = new HttpHost(targetedHost, targetedPort, scheme);
//		clone.ssl = ssl;
//		clone.body = ArrayUtils.copyOf(body);
//
//		return clone;
//	}
//
//	public boolean expectContinue()
//	{
//		return false;
//	}
//	
//	public HttpEntity getEntity()
//	{
//		return new ByteArrayEntity(body);
//	}
//	
//	public void setEntity(HttpEntity entity)
//	{
//		try
//		{
//			body = HttpUtils.entityToByteArray(entity, 0);
//		}
//		catch (IOException e)
//		{
//			LOGGER.error("Error setting entity: " + e.toString(), e);
//		}
//	}
//	
//	public byte[] getBody()
//    {
//    	return body;
//    }
//
//	public void setBody(byte[] body)
//    {
//    	this.body = body;
//    }
//
//}
