package com.grendelscan.proxy;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;

public class DefaultHttpProxyConnection extends DefaultHttpServerConnection
{
	@Override
	protected HttpRequestFactory createHttpRequestFactory()
	{
		return new CustomHttpRequestFactory();
	}
}
