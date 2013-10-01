package com.grendelscan.requester.http.apache_overrides.client;

import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.apache_overrides.requests.GenericRequestWithBody;

public class CustomHttpRequestExecutor extends HttpRequestExecutor
{

	public CustomHttpRequestExecutor()
    {
	    super();
    }

	@Override
    public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context)
            throws IOException, HttpException
    {
		Date start = new Date();
		HttpResponse response = super.execute(request, conn, context);
		Date end = new Date();
		long length = Math.round((double)(end.getTime() - start.getTime())/(double)1000);
		if (length > 15)
		{
			String uri;
			if (request instanceof GenericRequestWithBody)
            {
	            uri = ((GenericRequestWithBody)request).getStringURI();
            }
			else
            {
            	uri = "<" + request.getClass().toString() + ">";
            }
			Log.warn("WARNING: Server took " + length + " seconds to process request for " + uri);
		}
		return response;
    }

	
}
