/*
 * 
 * ==================================================================== Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the Apache Software Foundation. For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.grendelscan.commons.http.apache_overrides.client;

import java.io.IOException;
import java.net.URI;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;

/**
 * @author David Byrne
 * 
 */
public class CustomHttpClient extends DefaultHttpClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpClient.class);
    private final HttpContext contextTemplate;
    private static int failedRequests = 0;
    private final Object failedRequestsLock = new Object();

    private final CacheConfig apacheCacheConfig;
    private final CachingHttpClient cachingClient;

    /**
     * 
     * @param conman
     * @param params
     */
    public CustomHttpClient(final ClientConnectionManager conman, final HttpParams params, final HttpContext contextTemplate)
    {
        super(conman, params);
        this.contextTemplate = contextTemplate;

        apacheCacheConfig = new CacheConfig();
        apacheCacheConfig.setMaxCacheEntries(0);
        apacheCacheConfig.setMaxObjectSizeBytes(50000);
        CacheConfiguration ehCacheConfig = new CacheConfiguration("Cobra Cache", 50);
        ehCacheConfig.setOverflowToDisk(true);
        ehCacheConfig.setMaxElementsInMemory(50);
        // FIXME: This should be passed in or retrieved some way that doesn't create a dependency. - Jonathan Byrne 10/01/2013
        // ehCacheConfig.setDiskStorePath(Scan.getInstance().getOutputDirectory());

        Ehcache ehCache = new Cache(ehCacheConfig);
        ehCache.initialise();
        HttpCacheStorage storage = new EhcacheHttpCacheStorage(ehCache);
        cachingClient = new CachingHttpClient(this, storage, apacheCacheConfig);
    }

    public HttpResponse cacheExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException
    {
        try
        {
            return cachingClient.execute(target, request, context);
        }
        catch (IOException e)
        {
            throw new HttpException("Problem executing cache request", e);
        }
    }

    public void cacheExecute(final StandardHttpTransaction transaction) throws HttpException
    {
        HttpResponse response = cacheExecute(transaction.getRequestWrapper().getHttpHost(), transaction.getRequestWrapper().getRequest(), transaction.getRequestOptions().context);
        if (response == null)
        {
            LOGGER.info("No response returned for " + transaction.getRequestWrapper().getAbsoluteUriString());
        }
        else
        {
            transaction.setResponseWrapper(new HttpResponseWrapper(transaction.getId(), response));
        }
    }

    @Override
    protected RequestDirector createClientRequestDirector(final HttpRequestExecutor requestExec, final ClientConnectionManager conman, final ConnectionReuseStrategy reustrat, final ConnectionKeepAliveStrategy kastrat, final HttpRoutePlanner rouplan,
                    final HttpProcessor httpProcessor, final HttpRequestRetryHandler retryHandler, final RedirectStrategy redirectStrategy, final AuthenticationHandler targetAuthHandler, final AuthenticationHandler proxyAuthHandler,
                    final UserTokenHandler stateHandler, final HttpParams params)
    {
        return new CustomClientRequestDirector(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, proxyAuthHandler, stateHandler, params);
    }

    @Override
    protected HttpContext createHttpContext()
    {
        HttpContext context = new SyncBasicHttpContext(contextTemplate);
        return context;
    }

    @Override
    protected BasicHttpProcessor createHttpProcessor()
    {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestDefaultHeaders());
        // Required protocol interceptors
        httpproc.addInterceptor(new CustomRequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        // HTTP state management interceptors
        // httpproc.addInterceptor(new RequestAddCookies());
        // httpproc.addInterceptor(new ResponseProcessCookies());
        // HTTP authentication interceptors
        // httpproc.addInterceptor(new RequestTargetAuthentication());
        // httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }

    @Override
    protected HttpRoutePlanner createHttpRoutePlanner()
    {
        return new CustomHttpRoutePlanner(getConnectionManager());
    }

    @Override
    protected HttpRequestExecutor createRequestExecutor()
    {
        return new CustomHttpRequestExecutor();
    }

    public HttpResponse customExecute(final HttpHost target, final HttpRequest request, HttpContext context) throws HttpException
    {
        if (context == null)
        {
            context = createHttpContext();
        }
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);

        prepareRequest(request);
        int tries = 0;
        HttpResponse response = null;
        String message = "";
        while (response == null && tries++ < Scan.getScanSettings().getMaxRequestRetries())
        {
            try
            {
                response = super.execute(target, request, context);
            }
            catch (IOException e)
            {
                message += e.toString() + " ";
                LOGGER.error("ERROR: Connection failure to server: " + e.toString(), e);
            }
            synchronized (failedRequestsLock)
            {
                if (response == null)
                {
                    if (++failedRequests > Scan.getScanSettings().getMaxConsecutiveFailedRequests())
                    {
                        Scan.getInstance().shutdown("Maximum consecutive failed requests exceeded");
                    }
                }
                else
                {
                    failedRequests = 0;
                }
            }
        }
        if (response == null)
        {
            throw new HttpException("Request failed." + (message.equals("") ? "" : " This might be the cause: " + message));
        }
        return response;
    }

    public void customExecute(final StandardHttpTransaction transaction) throws HttpException
    {
        HttpResponse response = customExecute(transaction.getRequestWrapper().getHttpHost(), transaction.getRequestWrapper().getRequest(), transaction.getRequestOptions().context);
        if (response == null)
        {
            LOGGER.info("No response returned for " + transaction.getRequestWrapper().getAbsoluteUriString());
        }
        else
        {
            transaction.setResponseWrapper(new HttpResponseWrapper(transaction.getId(), response));
        }
    }

    public HttpContext getContextTemplate()
    {
        return contextTemplate;
    }

    public String getRequestHeadString(final HttpHost originalTarget, final HttpRequest request, final HttpContext originalContext) throws IOException, HttpException
    {
        HttpContext context = originalContext;
        HttpHost target = originalTarget;

        if (context == null)
        {
            context = createHttpContext();
        }

        if (target == null)
        {
            URI uri = ((HttpUriRequest) request).getURI();
            target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);
        prepareRequest(request);

        getHttpProcessor().process(request, context);

        String requestString = request.getRequestLine().toString() + "\n";
        for (Header header : request.getAllHeaders())
        {
            requestString += header.toString() + "\n";
        }
        return requestString;
    }

    private void prepareRequest(final HttpRequest request)
    {
        request.setParams(getParams());
    }

}
