package com.grendelscan.commons.http.factories;

import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpProtocolParams;

import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;

public class HttpRequestFactory
{

    /**
     * 
     * @param method
     * @param uri
     * @param headers
     * @param body
     * @param version
     * @return
     * @throws URISyntaxException
     */
    public static HttpRequest makeNonScanRequest(final String method, final String uri, final Header headers[], final byte[] body, final ProtocolVersion version)
    {
        HttpRequestBase request = null;
        boolean entity = false;

        if (method.equalsIgnoreCase(HttpGet.METHOD_NAME))
        {
            request = new HttpGet(uri);
        }
        else if (method.equalsIgnoreCase(HttpPut.METHOD_NAME))
        {
            entity = true;
            request = new HttpPut(uri);
        }
        else if (method.equalsIgnoreCase(HttpPost.METHOD_NAME))
        {
            request = new HttpPost(uri);
            entity = true;
        }
        else if (method.equalsIgnoreCase(HttpTrace.METHOD_NAME))
        {
            request = new HttpTrace(uri);
        }
        else if (method.equalsIgnoreCase(HttpOptions.METHOD_NAME))
        {
            request = new HttpOptions(uri);
        }
        else if (method.equalsIgnoreCase(HttpDelete.METHOD_NAME))
        {
            request = new HttpDelete(uri);
        }
        else if (method.equalsIgnoreCase(HttpHead.METHOD_NAME))
        {
            request = new HttpHead(uri);
        }

        if (entity && body != null)
        {
            ((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(body));
        }

        ProtocolVersion versionToUse = version;
        if (versionToUse == null)
        {
            versionToUse = HttpRequestWrapper.DEFAULT_PROTOCL_VERSION;
        }
        HttpProtocolParams.setVersion(request.getParams(), versionToUse);

        if (headers != null)
        {
            request.setHeaders(headers);
        }

        return request;
    }
}
