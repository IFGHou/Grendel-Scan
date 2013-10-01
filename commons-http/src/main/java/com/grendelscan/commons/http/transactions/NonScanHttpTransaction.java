package com.grendelscan.commons.http.transactions;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.grendelscan.commons.http.factories.HttpRequestFactory;
import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;
import com.grendelscan.scan.Scan;
import com.grendelscan.commons.http.HttpUtils;

public class NonScanHttpTransaction
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NonScanHttpTransaction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 256619227298731217L;
	private byte requestBody[];
	private byte responseBody[];
	
	private HttpUriRequest request;
	private HttpResponse response;
	private String uri;
	
	
	public NonScanHttpTransaction(String method, String uri)
	{
		this.uri = uri;
		request = (HttpUriRequest) HttpRequestFactory.makeNonScanRequest(method, this.uri, null, null, HttpRequestWrapper.DEFAULT_PROTOCL_VERSION);
	}
	
	public void addRequestHeader(Header header)
	{
		request.addHeader(header);
	}
	
	public void addRequestHeader(String name, String value)
	{
		request.addHeader(name, value);
	}
	
	public void execute(HttpClient client) throws IOException
	{
		int retries = 0;
		while (response == null && retries++ <= Scan.getScanSettings().getMaxRequestRetries())
		{
	        response = client.execute(request);
	        responseBody = HttpUtils.entityToByteArray(response.getEntity(), 0);
		}
	}
	
	public byte[] getRequestBody()
    {
    	return requestBody;
    }

	public byte[] getResponseBody()
    {
    	return responseBody;
    }

	public HttpResponse getResponse()
    {
    	return response;
    }


}
