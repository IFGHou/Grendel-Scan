package com.grendelscan.proxy.abstractProxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.grendelscan.GUI.http.interception.InterceptionComposite;
import com.grendelscan.GUI.proxy.interception.StandardInterceptFilter;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.Destination;
import com.grendelscan.requester.RequestOptions;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.HttpCloner;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.MimeUtils;
import com.grendelscan.utils.StringUtils;

public abstract class AbstractProxyRequestHandler implements HttpRequestHandler
{
	
	protected AbstractProxy proxy;
	protected int remoteSSLPort;
	protected boolean ssl;
	private final RequestOptions proxyRequestOptions;
	

	
	public AbstractProxyRequestHandler(AbstractProxy proxy, boolean ssl, int remoteSSLPort)
	{
		this.proxy = proxy;
		this.ssl = ssl;
		this.remoteSSLPort = remoteSSLPort;
		proxyRequestOptions = new RequestOptions();
		proxyRequestOptions.followRedirects = false;
		proxyRequestOptions.handleSessions = false;
		proxyRequestOptions.ignoreRestrictions = true;
		proxyRequestOptions.testTransaction = false; // Internal proxy logic will submit to categorizer as needed
		proxyRequestOptions.reason = "Internal proxy";
	}
	
//	private StandardHttpTransaction getRequestToUse(HttpRequest request) throws IOException
//	{
//		Destination destination = getDestination(request);
//		String host = destination.host;
//		int port = destination.port;
//		String uri = request.getRequestLine().getUri();
//		if (ssl)
//		{
//			port = remoteSSLPort;
//		}
//		UnvalidatedHttpRequest requestToUse;
//		byte body[] = null;
//		if (request instanceof BasicHttpEntityEnclosingRequest)
//		{
//			HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
//			body = HttpUtils.entityToByteArray(entity, 0);
//			requestToUse = new UnvalidatedHttpEntityRequest(request.getRequestLine().getMethod(), uri, host, port, ssl, body);
//		}
//		else
//		{
//			 requestToUse = new UnvalidatedHttpRequest(request.getRequestLine().getMethod(), uri, host, port, ssl);
//		}
//		requestToUse.addHeaders(request.getAllHeaders());
//		fixRequestHeaders(requestToUse);
//		return requestToUse;
//	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws IOException
	{
		Log.debug("Proxy received request for: " + request.getRequestLine().getUri());
		
		
		StandardHttpTransaction transaction = new StandardHttpTransaction(TransactionSource.PROXY, -1);
		transaction.getRequestWrapper().getHeaders().addHeaders(request.getAllHeaders());
		transaction.getRequestWrapper().setSecure(ssl);
		transaction.getRequestWrapper().setMethod(request.getRequestLine().getMethod());
		transaction.getRequestWrapper().setURI(request.getRequestLine().getUri(), true);
		transaction.getRequestWrapper().setVersion(request.getRequestLine().getProtocolVersion());
		transaction.setRequestOptions(proxyRequestOptions);
		if (request instanceof BasicHttpEntityEnclosingRequest)
		{
			HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
			transaction.getRequestWrapper().setBody(HttpUtils.entityToByteArray(entity, 0));
		}
		
		boolean testUninterceptedTransaction = Scan.getScanSettings().isTestProxyRequests();
		
		
		if (!Scan.getScanSettings().getUrlFilters().isUriAllowed(transaction.getRequestWrapper().getAbsoluteUriString()))
		{
			if (Scan.getScanSettings().isAllowAllProxyRequests())
			{
				testUninterceptedTransaction = false;
			}
			else
			{
				forbiddenMessage(request, response);
				return;
			}
		}
		
	
		StandardHttpTransaction postInterceptTransaction = checkRequestIntercept(transaction);
		boolean intercepted = false;
		if (postInterceptTransaction != transaction)
		{
			transaction = postInterceptTransaction;
			intercepted = true;
		}

		try
		{
			transaction.execute();
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn("Proxy request unrequestable (" + transaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
		}
		catch (InterruptedScanException e)
		{
			Log.info("Scan aborted: " + e.toString(), e);
			return;
		}

		intercepted = checkResponseIntercept(transaction) || intercepted;
		if (testUninterceptedTransaction || (intercepted && Scan.getScanSettings().isTestInterceptedRequests()))
		{
			Scan.getInstance().getCategorizerQueue().addTransaction(transaction);
		}
			
		if (transaction.getResponseWrapper() == null)
		{
			errorMessage(response, 500, "Proxy error", "Unknown problem with Grendel-Scan proxy. The remote host may be unreachable");
		}
		else
		{
			HttpEntity entity = new ByteArrayEntity(getProcessedResponseBody(transaction));
			response.setEntity(entity);
			for (Header header: transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
			{
				response.addHeader(HttpCloner.clone(header));
			}
			response.setStatusLine(HttpCloner.clone(transaction.getResponseWrapper().getStatusLine()));
		}
	}

	
	
	protected StandardHttpTransaction checkRequestIntercept(StandardHttpTransaction transaction)
	{
		if (Scan.getScanSettings().isInterceptRequests())
		{
			if (StandardInterceptFilter.matchesFilters(Scan.getScanSettings().getReadOnlyRequestInterceptFilters(), transaction))
			{
				return InterceptionComposite.showRequestIntercept(transaction);
			}
		}
		return transaction;
	}
	
	/**
	 * This doesn't need to return a transaction because the response intercept
	 * will never create a new transaction
	 * @param transaction
	 * @return
	 */
	protected boolean checkResponseIntercept(StandardHttpTransaction transaction)
	{
		if (Scan.getScanSettings().isInterceptResponses())
		{
			if (StandardInterceptFilter.matchesFilters(Scan.getScanSettings().getReadOnlyResponseInterceptFilters(), transaction))
			{
				return InterceptionComposite.showResponseIntercept(transaction);
			}
		}
		return false;
	}
	
	static final Pattern typeChangePattern = Pattern.compile("type\\s*=\\s*['\"]?hidden['\"]?+", 
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	

	static final Pattern hiddenFieldsPattern = Pattern.compile(
			"(<input[^>]*\\s*type\\s*=\\s*[\"']?hidden[\"']?[^>]*>)", 
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	
	
	static final Pattern fieldNameQuotePattern = Pattern.compile(
			"name\\s*=\\s*(.)", 
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	
	protected String getNameAttributeQuoteCharacter(String html)
	{
		Matcher m = fieldNameQuotePattern.matcher(html);
		if (m.find())
		{
			String quote = m.group(1);
			if (quote.equals("'") || quote.equals("\""))
			{
				return quote;
			}
		}
		return "";
	}
	
	protected byte[] getProcessedResponseBody(StandardHttpTransaction transaction)
	{
		byte[] body = transaction.getResponseWrapper().getBody();
		if (body == null || body.length == 0)
		{
			return new byte[0];
		}
		
		if (Scan.getScanSettings().isRevealHiddenFields() && MimeUtils.isHtmlMimeType(transaction.getResponseWrapper().getHeaders().getMimeType()))
		{
			String responseBody = new String(transaction.getResponseWrapper().getBody(), StringUtils.getDefaultCharset());
			while (true)
			{
				Matcher hiddenFieldMatcher = hiddenFieldsPattern.matcher(responseBody);
				if (hiddenFieldMatcher.find()) // We found a hidden field
				{
					
					String field = hiddenFieldMatcher.group(1);
					String quote = getNameAttributeQuoteCharacter(field);
					Pattern fieldNamePattern;
					if (!quote.equals(""))
					{
						fieldNamePattern = Pattern.compile(
							"name\\s*=\\s*" + quote + "([^" + quote + "]+)" + quote, 
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					}
					else
					{
						fieldNamePattern = Pattern.compile(
							"name\\s*=\\s*(\\S+)", 
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					}

					Matcher fieldNameMatcher = fieldNamePattern.matcher(field);
					
					String name = "Unknown name";
					if(fieldNameMatcher.find())
					{
						name = fieldNameMatcher.group(1);
					}

					String newField = typeChangePattern.matcher(field).replaceFirst("type=\"text\"");
					responseBody = responseBody.replace(field, "\n<br/><span style=\"font-weight: bold; color: black;background-color: white\">" +
												"Hidden field \"" + name + "\": " + newField + "</span><br/>\n");
//					responseBody = hiddenFieldMatcher.replaceFirst(
//							"<br><span style=\"font-weight: bold; color: black;background-color: white\">" +
//							"Hidden field \"" + name + "\"$1 type=\"text\" $2</span><br>");
				}
				else
				{
					break;
				}
			}
			body = responseBody.getBytes(StringUtils.getDefaultCharset());
		}
		else
		{
			body =  transaction.getResponseWrapper().getBody();
		}
		
		return body;
	}
	
	protected void forbiddenMessage(HttpRequest httpRequest, HttpResponse response)
	{
		String body =
		        "The scanner is not allowed to access the requested URL (" + httpRequest.getRequestLine().getUri()
		                + ").";
		errorMessage(response, 403, "Forbidden", body);
	}
	
	protected void errorMessage(HttpResponse response, int code, String reason, String body)
	{
		response.setStatusCode(code);
		response.setReasonPhrase(reason);
		StringEntity entity = null;
		try
		{
			entity = new StringEntity(body);
		}
		catch (UnsupportedEncodingException e)
		{
			Log.error("Unsupported encoding: " + e.toString(), e);
		}
		response.setEntity(entity);
		response.addHeader("Server", "Grendel-Scan proxy");
		response.addHeader("Content-Length", String.valueOf(body.length()));
	}

//	protected void fixRequestHeaders(UnvalidatedHttpRequest request)
//	{
//		// Get rid of proxy and connection-related headers
//		request.removeHeaders("Proxy-Connection");
//		request.removeHeaders("Connection");
//		request.removeHeaders("Keep-Alive");
//		
//		// An easy way to get rid of compression. Need a more elegant solution to handle it later
//		request.removeHeaders("Accept-Encoding");
//		
//	//	request.removeHeaders("Proxy-Authorization");
//
//	}
	
	protected abstract Destination getDestination(HttpRequest request);
}
