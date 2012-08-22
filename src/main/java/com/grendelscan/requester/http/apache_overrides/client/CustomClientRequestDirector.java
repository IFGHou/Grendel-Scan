/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.grendelscan.requester.http.apache_overrides.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.TunnelRefusedException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import com.grendelscan.logging.Log;

/**
 * Default implementation of {@link RequestDirector}.
 * <p>
 * The following parameters can be used to customize the behavior of this class:
 * <ul>
 * <li>{@link org.apache.http.params.CoreProtocolPNames#PROTOCOL_VERSION}</li>
 * <li>
 * {@link org.apache.http.params.CoreProtocolPNames#STRICT_TRANSFER_ENCODING}</li>
 * <li>{@link org.apache.http.params.CoreProtocolPNames#HTTP_ELEMENT_CHARSET}</li>
 * <li>{@link org.apache.http.params.CoreProtocolPNames#USE_EXPECT_CONTINUE}</li>
 * <li>{@link org.apache.http.params.CoreProtocolPNames#WAIT_FOR_CONTINUE}</li>
 * <li>{@link org.apache.http.params.CoreProtocolPNames#USER_AGENT}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#SOCKET_BUFFER_SIZE}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#MAX_LINE_LENGTH}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#MAX_HEADER_COUNT}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#SO_TIMEOUT}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#SO_LINGER}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#TCP_NODELAY}</li>
 * <li>{@link org.apache.http.params.CoreConnectionPNames#CONNECTION_TIMEOUT}</li>
 * <li>
 * {@link org.apache.http.params.CoreConnectionPNames#STALE_CONNECTION_CHECK}</li>
 * <li>{@link org.apache.http.conn.params.ConnRoutePNames#FORCED_ROUTE}</li>
 * <li>{@link org.apache.http.conn.params.ConnRoutePNames#LOCAL_ADDRESS}</li>
 * <li>{@link org.apache.http.conn.params.ConnRoutePNames#DEFAULT_PROXY}</li>
 * <li>{@link org.apache.http.conn.params.ConnManagerPNames#TIMEOUT}</li>
 * <li>
 * {@link org.apache.http.conn.params.ConnManagerPNames#MAX_CONNECTIONS_PER_ROUTE}
 * </li>
 * <li>
 * {@link org.apache.http.conn.params.ConnManagerPNames#MAX_TOTAL_CONNECTIONS}</li>
 * <li>{@link org.apache.http.cookie.params.CookieSpecPNames#DATE_PATTERNS}</li>
 * <li>
 * {@link org.apache.http.cookie.params.CookieSpecPNames#SINGLE_COOKIE_HEADER}</li>
 * <li>{@link org.apache.http.auth.params.AuthPNames#CREDENTIAL_CHARSET}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#COOKIE_POLICY}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#HANDLE_AUTHENTICATION}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#HANDLE_REDIRECTS}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#MAX_REDIRECTS}</li>
 * <li>
 * {@link org.apache.http.client.params.ClientPNames#ALLOW_CIRCULAR_REDIRECTS}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#VIRTUAL_HOST}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#DEFAULT_HOST}</li>
 * <li>{@link org.apache.http.client.params.ClientPNames#DEFAULT_HEADERS}</li>
 * </ul>
 * 
 * @since 4.0
 */
@NotThreadSafe
// e.g. managedConn
public class CustomClientRequestDirector implements RequestDirector
{


	private HttpHost							virtualHost;

	/** The connection manager. */
	private final ClientConnectionManager		connManager;

	/** The HTTP protocol processor. */
	private final HttpProcessor				httpProcessor;

	/** The keep-alive duration strategy. */
	private final ConnectionKeepAliveStrategy	keepAliveStrategy;

	/** The currently allocated connection. */
	private ManagedClientConnection			managedConn;

	/** The HTTP parameters. */
	private final HttpParams					params;

	/** The proxy authentication handler. */
	private final AuthenticationHandler		proxyAuthHandler;

	private final AuthState					proxyAuthState;

	/** The request executor. */
	private final HttpRequestExecutor			requestExec;


	/** The connection re-use strategy. */
	private final ConnectionReuseStrategy		reuseStrategy;

	/** The route planner. */
	private final HttpRoutePlanner			routePlanner;

	private final AuthState					targetAuthState;

	/** The user token handler. */
	private final UserTokenHandler			userTokenHandler;


	public CustomClientRequestDirector(
			final HttpRequestExecutor requestExec,
			final ClientConnectionManager conman,
			final ConnectionReuseStrategy reustrat,
			final ConnectionKeepAliveStrategy kastrat,
			final HttpRoutePlanner rouplan,
			final HttpProcessor httpProcessor,
			final AuthenticationHandler proxyAuthHandler,
			final UserTokenHandler userTokenHandler,
			final HttpParams params)
	{

		if (requestExec == null)
		{
			throw new IllegalArgumentException("Request executor may not be null.");
		}
		if (conman == null)
		{
			throw new IllegalArgumentException("Client connection manager may not be null.");
		}
		if (reustrat == null)
		{
			throw new IllegalArgumentException("Connection reuse strategy may not be null.");
		}
		if (kastrat == null)
		{
			throw new IllegalArgumentException("Connection keep alive strategy may not be null.");
		}
		if (rouplan == null)
		{
			throw new IllegalArgumentException("Route planner may not be null.");
		}
		if (httpProcessor == null)
		{
			throw new IllegalArgumentException("HTTP protocol processor may not be null.");
		}
		if (proxyAuthHandler == null)
		{
			throw new IllegalArgumentException("Proxy authentication handler may not be null.");
		}
		if (userTokenHandler == null)
		{
			throw new IllegalArgumentException("User token handler may not be null.");
		}
		if (params == null)
		{
			throw new IllegalArgumentException("HTTP parameters may not be null");
		}
		this.requestExec = requestExec;
		connManager = conman;
		reuseStrategy = reustrat;
		keepAliveStrategy = kastrat;
		routePlanner = rouplan;
		this.httpProcessor = httpProcessor;
		this.proxyAuthHandler = proxyAuthHandler;
		this.userTokenHandler = userTokenHandler;
		this.params = params;

		managedConn = null;
		targetAuthState = new AuthState();
		proxyAuthState = new AuthState();
	} // constructor

	// non-javadoc, see interface ClientRequestDirector
	@Override
	public HttpResponse execute(HttpHost originalTarget, final HttpRequest request,
			HttpContext context)
			throws HttpException, IOException
	{
		HttpHost target = originalTarget;
		final HttpRoute route = determineRoute(target, request, context);

		virtualHost = (HttpHost) request.getParams().getParameter(
				ClientPNames.VIRTUAL_HOST);

		long timeout = ConnManagerParams.getTimeout(params);

		try
		{
			HttpResponse response = null;

			// See if we have a user token bound to the execution context
			Object userToken = context.getAttribute(ClientContext.USER_TOKEN);

			// Allocate connection if needed
			if (managedConn == null)
			{
				ClientConnectionRequest connRequest = connManager.requestConnection(
						route, userToken);
				if (request instanceof AbortableHttpRequest)
				{
					((AbortableHttpRequest) request).setConnectionRequest(connRequest);
				}

				try
				{
					managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException interrupted)
				{
					InterruptedIOException iox = new InterruptedIOException();
					iox.initCause(interrupted);
					throw iox;
				}

				if (HttpConnectionParams.isStaleCheckingEnabled(params))
				{
					// validate connection
					if (managedConn.isOpen())
					{
						Log.debug("Stale connection check");
						if (managedConn.isStale())
						{
							Log.debug("Stale connection detected");
							managedConn.close();
						}
					}
				}
			}

			if (request instanceof AbortableHttpRequest)
			{
				((AbortableHttpRequest) request).setReleaseTrigger(managedConn);
			}

			// Reopen connection if needed
			if (!managedConn.isOpen())
			{
				managedConn.open(route, context, params);
			}
			else
			{
				managedConn.setSocketTimeout(HttpConnectionParams.getSoTimeout(params));
			}

			try
			{
				establishRoute(route, context);
			}
			catch (TunnelRefusedException ex)
			{
				Log.debug(ex.getMessage());
				response = ex.getResponse();
			}

			// Use virtual host if set
			target = virtualHost;

			if (target == null)
			{
				target = route.getTargetHost();
			}

			HttpHost proxy = route.getProxyHost();

			// Populate the execution context
			context.setAttribute(ExecutionContext.HTTP_TARGET_HOST,
					target);
			context.setAttribute(ExecutionContext.HTTP_PROXY_HOST,
					proxy);
			context.setAttribute(ExecutionContext.HTTP_CONNECTION,
					managedConn);
			context.setAttribute(ClientContext.TARGET_AUTH_STATE,
					targetAuthState);
			context.setAttribute(ClientContext.PROXY_AUTH_STATE,
					proxyAuthState);

			// Run request protocol interceptors
			requestExec.preProcess(request, httpProcessor, context);

			try
			{
				response = requestExec.execute(request, managedConn, context);
			}
			catch (IOException ex)
			{
				Log.debug("Closing connection after request failure.");
				managedConn.close();
				throw ex;
			}

			if (response == null)
			{
				return null;
			}

			// Run response protocol interceptors
			response.setParams(params);
			requestExec.postProcess(response, httpProcessor, context);

			// The connection is in or can be brought to a re-usable state.
			boolean reuse = reuseStrategy.keepAlive(response, context);
			if (reuse)
			{
				// Set the idle duration of this connection
				long duration = keepAliveStrategy.getKeepAliveDuration(response, context);
				managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);

				if (duration >= 0)
				{
					Log.trace("Connection can be kept alive for " + duration + " ms");
				}
				else
				{
					Log.trace("Connection can be kept alive indefinitely");
				}
			}


			if ((managedConn != null) && (userToken == null))
			{
				userToken = userTokenHandler.getUserToken(context);
				context.setAttribute(ClientContext.USER_TOKEN, userToken);
				if (userToken != null)
				{
					managedConn.setState(userToken);
				}
			}

			// check for entity, release connection if possible
			if ((response.getEntity() == null) || !response.getEntity().isStreaming())
			{
				// connection not needed and (assumed to be) in re-usable state
				if (reuse)
				{
					managedConn.markReusable();
				}
				releaseConnection();
			}
			else
			{
				// install an auto-release entity
				HttpEntity entity = response.getEntity();
				entity = new BasicManagedEntity(entity, managedConn, reuse);
				response.setEntity(entity);
			}

			return response;

		}
		catch (HttpException ex)
		{
			abortConnection();
			throw ex;
		}
		catch (IOException ex)
		{
			abortConnection();
			throw ex;
		}
		catch (RuntimeException ex)
		{
			abortConnection();
			throw ex;
		}
	} // execute

	/**
	 * Shuts down the connection.
	 * This method is called from a <code>catch</code> block in {@link #execute
	 * execute} during exception handling.
	 */
	private void abortConnection()
	{
		ManagedClientConnection mcc = managedConn;
		if (mcc != null)
		{
			// we got here as the result of an exception
			// no response will be returned, release the connection
			managedConn = null;
			try
			{
				mcc.abortConnection();
			}
			catch (IOException ex)
			{
				Log.trace(ex.getMessage(), ex);
			}
			// ensure the connection manager properly releases this connection
			try
			{
				mcc.releaseConnection();
			}
			catch (IOException ignored)
			{
				Log.debug("Error releasing connection", ignored);
			}
		}
	} // abortConnection

	private void processChallenges(
			final Map<String, Header> challenges,
			final AuthState authState,
			final AuthenticationHandler authHandler,
			final HttpResponse response,
			final HttpContext context)
			throws MalformedChallengeException, AuthenticationException
	{

		AuthScheme authScheme = authState.getAuthScheme();
		if (authScheme == null)
		{
			// Authentication not attempted before
			authScheme = authHandler.selectScheme(challenges, response, context);
			authState.setAuthScheme(authScheme);
		}
		String id = authScheme.getSchemeName();

		Header challenge = challenges.get(id.toLowerCase(Locale.ENGLISH));
		if (challenge == null)
		{
			throw new AuthenticationException(id +
					" authorization challenge expected, but not found");
		}
		authScheme.processChallenge(challenge);
		Log.debug("Authorization challenge processed");
	}

	
	private void updateAuthState(
			final AuthState authState,
			final HttpHost host,
			final CredentialsProvider credsProvider)
	{
		if (!authState.isValid())
		{
			return;
		}

		String hostname = host.getHostName();
		int port = host.getPort();
		if (port < 0)
		{
			Scheme scheme = connManager.getSchemeRegistry().getScheme(host);
			port = scheme.getDefaultPort();
		}

		AuthScheme authScheme = authState.getAuthScheme();
		AuthScope authScope = new AuthScope(
				hostname,
				port,
				authScheme.getRealm(),
				authScheme.getSchemeName());

		Log.trace("Authentication scope: " + authScope);
		Credentials creds = authState.getCredentials();
		if (creds == null)
		{
			creds = credsProvider.getCredentials(authScope);
			if (creds != null)
			{
				Log.trace("Found credentials");
			}
			else
			{
				Log.trace("Credentials not found");
			}
		}
		else
		{
			if (authScheme.isComplete())
			{
				Log.debug("Authentication failed");
				creds = null;
			}
		}
		authState.setAuthScope(authScope);
		authState.setCredentials(creds);
	}

//	private RequestWrapper wrapRequest(
//			final HttpRequest request) throws ProtocolException
//	{
//		if (request instanceof HttpEntityEnclosingRequest)
//		{
//			return new EntityEnclosingRequestWrapper(
//					(HttpEntityEnclosingRequest) request);
//		}
//		else
//		{
//			return new RequestWrapper(
//					request);
//		}
//	}

	/**
	 * Creates the CONNECT request for tunnelling.
	 * Called by {@link #createTunnelToTarget createTunnelToTarget}.
	 * 
	 * @param route
	 *            the route to establish
	 * @param context
	 *            the context for request execution
	 * 
	 * @return the CONNECT request for tunnelling
	 */
	private HttpRequest createConnectRequest(HttpRoute route)
	{
		// see RFC 2817, section 5.2 and
		// INTERNET-DRAFT: Tunneling TCP based protocols through
		// Web proxy servers

		HttpHost target = route.getTargetHost();

		String host = target.getHostName();
		int port = target.getPort();
		if (port < 0)
		{
			Scheme scheme = connManager.getSchemeRegistry().
					getScheme(target.getSchemeName());
			port = scheme.getDefaultPort();
		}

		StringBuilder buffer = new StringBuilder(host.length() + 6);
		buffer.append(host);
		buffer.append(':');
		buffer.append(Integer.toString(port));

		String authority = buffer.toString();
		ProtocolVersion ver = HttpProtocolParams.getVersion(params);
		HttpRequest req = new BasicHttpRequest
				("CONNECT", authority, ver);

		return req;
	}


	/**
	 * Creates a tunnel to the target server.
	 * The connection must be established to the (last) proxy.
	 * A CONNECT request for tunnelling through the proxy will
	 * be created and sent, the response received and checked.
	 * This method does <i>not</i> update the connection with
	 * information about the tunnel, that is left to the caller.
	 * 
	 * @param route
	 *            the route to establish
	 * @param context
	 *            the context for request execution
	 * 
	 * @return <code>true</code> if the tunnelled route is secure,
	 *         <code>false</code> otherwise.
	 *         The implementation here always returns <code>false</code>,
	 *         but derived classes may override.
	 * 
	 * @throws HttpException
	 *             in case of a problem
	 * @throws IOException
	 *             in case of an IO problem
	 */
	private boolean createTunnelToTarget(HttpRoute route,
			HttpContext context)
			throws HttpException, IOException
	{

		HttpHost proxy = route.getProxyHost();
		HttpHost target = route.getTargetHost();
		HttpResponse response = null;

		boolean done = false;
		while (!done)
		{

			done = true;

			if (!managedConn.isOpen())
			{
				managedConn.open(route, context, params);
			}

			HttpRequest connect = createConnectRequest(route);
			connect.setParams(params);

			// Populate the execution context
			context.setAttribute(ExecutionContext.HTTP_TARGET_HOST,
					target);
			context.setAttribute(ExecutionContext.HTTP_PROXY_HOST,
					proxy);
			context.setAttribute(ExecutionContext.HTTP_CONNECTION,
					managedConn);
			context.setAttribute(ClientContext.TARGET_AUTH_STATE,
					targetAuthState);
			context.setAttribute(ClientContext.PROXY_AUTH_STATE,
					proxyAuthState);
			context.setAttribute(ExecutionContext.HTTP_REQUEST,
					connect);

			requestExec.preProcess(connect, httpProcessor, context);

			response = requestExec.execute(connect, managedConn, context);

			response.setParams(params);
			requestExec.postProcess(response, httpProcessor, context);

			int status = response.getStatusLine().getStatusCode();
			if (status < 200)
			{
				throw new HttpException("Unexpected response to CONNECT request: " +
						response.getStatusLine());
			}

			CredentialsProvider credsProvider = (CredentialsProvider)
					context.getAttribute(ClientContext.CREDS_PROVIDER);

			if ((credsProvider != null) && HttpClientParams.isAuthenticating(params))
			{
				if (proxyAuthHandler.isAuthenticationRequested(response, context))
				{

					Log.debug("Proxy requested authentication");
					Map<String, Header> challenges = proxyAuthHandler.getChallenges(
							response, context);
					try
					{
						processChallenges(
								challenges, proxyAuthState, proxyAuthHandler,
								response, context);
					}
					catch (AuthenticationException ex)
					{
						Log.warn("Authentication error: " + ex.getMessage());
					}
					updateAuthState(proxyAuthState, proxy, credsProvider);

					if (proxyAuthState.getCredentials() != null)
					{
						done = false;

						// Retry request
						if (reuseStrategy.keepAlive(response, context))
						{
							Log.debug("Connection kept alive");
							// Consume response content
							HttpEntity entity = response.getEntity();
							if (entity != null)
							{
								entity.consumeContent();
							}
						}
						else
						{
							managedConn.close();
						}

					}

				}
				else
				{
					// Reset proxy auth scope
					proxyAuthState.setAuthScope(null);
				}
			}
		}

		int status = response.getStatusLine().getStatusCode(); // can't be null

		if (status > 299)
		{

			// Buffer response content
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				response.setEntity(new BufferedHttpEntity(entity));
			}

			managedConn.close();
			throw new TunnelRefusedException("CONNECT refused by proxy: " +
					response.getStatusLine(), response);
		}

		managedConn.markReusable();

		// How to decide on security of the tunnelled connection?
		// The socket factory knows only about the segment to the proxy.
		// Even if that is secure, the hop to the target may be insecure.
		// Leave it to derived classes, consider insecure by default here.
		return false;

	} // createTunnelToTarget

	/**
	 * Determines the route for a request.
	 * Called by {@link #execute} to determine the route for either the original
	 * or a followup request.
	 * 
	 * @param target
	 *            the target host for the request.
	 *            Implementations may accept <code>null</code> if they can still
	 *            determine a route, for example
	 *            to a default target or by inspecting the request.
	 * @param request
	 *            the request to execute
	 * @param context
	 *            the context to use for the execution,
	 *            never <code>null</code>
	 * 
	 * @return the route the request should take
	 * 
	 * @throws HttpException
	 *             in case of a problem
	 */
	private HttpRoute determineRoute(HttpHost originalTarget,
			HttpRequest request,
			HttpContext context)
			throws HttpException
	{
		HttpHost target = originalTarget;
		if (target == null)
		{
			target = (HttpHost) request.getParams().getParameter(
					ClientPNames.DEFAULT_HOST);
		}
		if (target == null)
		{
			throw new IllegalStateException("Target host must not be null, or set in parameters.");
		}

		return routePlanner.determineRoute(target, request, context);
	}

	/**
	 * Establishes the target route.
	 * 
	 * @param route
	 *            the route to establish
	 * @param context
	 *            the context for the request execution
	 * 
	 * @throws HttpException
	 *             in case of a problem
	 * @throws IOException
	 *             in case of an IO problem
	 */
	private void establishRoute(HttpRoute route, HttpContext context)
			throws HttpException, IOException
	{

		HttpRouteDirector rowdy = new BasicRouteDirector();
		int step;
		do
		{
			HttpRoute fact = managedConn.getRoute();
			step = rowdy.nextStep(route, fact);

			switch (step)
			{

				case HttpRouteDirector.CONNECT_TARGET:
				case HttpRouteDirector.CONNECT_PROXY:
					managedConn.open(route, context, params);
					break;

				case HttpRouteDirector.TUNNEL_TARGET:
				{
					boolean secure = createTunnelToTarget(route, context);
					Log.debug("Tunnel to target created.");
					managedConn.tunnelTarget(secure, params);
				}
					break;

				case HttpRouteDirector.TUNNEL_PROXY:
				{
					throw new NotImplementedException("Proxy chaining not supported");
				}

				case HttpRouteDirector.LAYER_PROTOCOL:
					managedConn.layerProtocol(context, params);
					break;

				case HttpRouteDirector.UNREACHABLE:
					throw new IllegalStateException("Unable to establish route." +
							"\nplanned = " + route +
							"\ncurrent = " + fact);

				case HttpRouteDirector.COMPLETE:
					// do nothing
					break;

				default:
					throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
			} // switch

		}
		while (step > HttpRouteDirector.COMPLETE);

	} // establishConnection


	/**
	 * Returns the connection back to the connection manager
	 * and prepares for retrieving a new connection during
	 * the next request.
	 */
	private void releaseConnection()
	{
		// Release the connection through the ManagedConnection instead of the
		// ConnectionManager directly. This lets the connection control how
		// it is released.
		try
		{
			managedConn.releaseConnection();
		}
		catch (IOException ignored)
		{
			Log.debug("IOException releasing connection", ignored);
		}
		managedConn = null;
	}


} // class DefaultClientRequestDirector
