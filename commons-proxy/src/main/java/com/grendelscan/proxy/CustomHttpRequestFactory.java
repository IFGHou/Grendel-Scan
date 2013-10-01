package com.grendelscan.proxy;

/*
 * ==================================================================== Licensed
 * to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The ASF licenses this file to you
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Apache Software Foundation. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 * 
 */

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on the DefaultHttpRequestFactory in Apache HttpComponents
 * @author David Byrne
 *
 */
public class CustomHttpRequestFactory implements HttpRequestFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpRequestFactory.class);
	
	public CustomHttpRequestFactory()
	{
		super();
	}
	
	@Override
	public HttpRequest newHttpRequest(final RequestLine requestline) throws MethodNotSupportedException
	{
		if (requestline == null)
		{
			IllegalArgumentException e = new IllegalArgumentException("Request line may not be null");
			LOGGER.error("Null request line in CustomHttpRequestFactory.newHttpRequest", e);
			throw e;
		}
		return newHttpRequest(requestline.getMethod(), requestline.getUri());
	}
	
	@Override
	public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException
	{
		LOGGER.trace("Creating new HTTP request object for \"" + method + " " + uri + "\"");
		if (method.equals("GET") || method.equals("HEAD") || method.equals("CONNECT") || method.equals("DELETE"))
		{
			return new BasicHttpRequest(method, uri);
		}
		else if (method.equals("POST") || method.equals("PUT"))
		{
			return new BasicHttpEntityEnclosingRequest(method, uri);
		}
		else
		{
			MethodNotSupportedException e = new MethodNotSupportedException(method + " method not supported");
			LOGGER.error("Method \"" + method + "\" isn't supported", e);
			throw e;
		}
	}
	
}
