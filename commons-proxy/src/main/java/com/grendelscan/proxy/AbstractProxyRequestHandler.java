package com.grendelscan.proxy;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.HttpCloner;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;

public abstract class AbstractProxyRequestHandler implements HttpRequestHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProxyRequestHandler.class);

    protected AbstractProxy proxy;
    protected int remoteSSLPort;
    protected boolean ssl;
    private final RequestOptions proxyRequestOptions;

    static final Pattern typeChangePattern = Pattern.compile("type\\s*=\\s*['\"]?hidden['\"]?+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // private StandardHttpTransaction getRequestToUse(HttpRequest request) throws IOException
    // {
    // Destination destination = getDestination(request);
    // String host = destination.host;
    // int port = destination.port;
    // String uri = request.getRequestLine().getUri();
    // if (ssl)
    // {
    // port = remoteSSLPort;
    // }
    // UnvalidatedHttpRequest requestToUse;
    // byte body[] = null;
    // if (request instanceof BasicHttpEntityEnclosingRequest)
    // {
    // HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request).getEntity();
    // body = HttpUtils.entityToByteArray(entity, 0);
    // requestToUse = new UnvalidatedHttpEntityRequest(request.getRequestLine().getMethod(), uri, host, port, ssl, body);
    // }
    // else
    // {
    // requestToUse = new UnvalidatedHttpRequest(request.getRequestLine().getMethod(), uri, host, port, ssl);
    // }
    // requestToUse.addHeaders(request.getAllHeaders());
    // fixRequestHeaders(requestToUse);
    // return requestToUse;
    // }

    static final Pattern hiddenFieldsPattern = Pattern.compile("(<input[^>]*\\s*type\\s*=\\s*[\"']?hidden[\"']?[^>]*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    static final Pattern fieldNameQuotePattern = Pattern.compile("name\\s*=\\s*(.)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public AbstractProxyRequestHandler(final AbstractProxy proxy, final boolean ssl, final int remoteSSLPort)
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

    protected StandardHttpTransaction checkRequestIntercept(final StandardHttpTransaction transaction)
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
     * This doesn't need to return a transaction because the response intercept will never create a new transaction
     * 
     * @param transaction
     * @return
     */
    protected boolean checkResponseIntercept(final StandardHttpTransaction transaction)
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

    protected void errorMessage(final HttpResponse response, final int code, final String reason, final String body)
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
            LOGGER.error("Unsupported encoding: " + e.toString(), e);
        }
        response.setEntity(entity);
        response.addHeader("Server", "Grendel-Scan proxy");
        response.addHeader("Content-Length", String.valueOf(body.length()));
    }

    protected void forbiddenMessage(final HttpRequest httpRequest, final HttpResponse response)
    {
        String body = "The scanner is not allowed to access the requested URL (" + httpRequest.getRequestLine().getUri() + ").";
        errorMessage(response, 403, "Forbidden", body);
    }

    protected abstract Destination getDestination(HttpRequest request);

    protected String getNameAttributeQuoteCharacter(final String html)
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

    protected byte[] getProcessedResponseBody(final StandardHttpTransaction transaction)
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
                        fieldNamePattern = Pattern.compile("name\\s*=\\s*" + quote + "([^" + quote + "]+)" + quote, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    }
                    else
                    {
                        fieldNamePattern = Pattern.compile("name\\s*=\\s*(\\S+)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    }

                    Matcher fieldNameMatcher = fieldNamePattern.matcher(field);

                    String name = "Unknown name";
                    if (fieldNameMatcher.find())
                    {
                        name = fieldNameMatcher.group(1);
                    }

                    String newField = typeChangePattern.matcher(field).replaceFirst("type=\"text\"");
                    responseBody = responseBody.replace(field, "\n<br/><span style=\"font-weight: bold; color: black;background-color: white\">" + "Hidden field \"" + name + "\": " + newField + "</span><br/>\n");
                    // responseBody = hiddenFieldMatcher.replaceFirst(
                    // "<br><span style=\"font-weight: bold; color: black;background-color: white\">" +
                    // "Hidden field \"" + name + "\"$1 type=\"text\" $2</span><br>");
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
            body = transaction.getResponseWrapper().getBody();
        }

        return body;
    }

    // protected void fixRequestHeaders(UnvalidatedHttpRequest request)
    // {
    // // Get rid of proxy and connection-related headers
    // request.removeHeaders("Proxy-Connection");
    // request.removeHeaders("Connection");
    // request.removeHeaders("Keep-Alive");
    //
    // // An easy way to get rid of compression. Need a more elegant solution to handle it later
    // request.removeHeaders("Accept-Encoding");
    //
    // // request.removeHeaders("Proxy-Authorization");
    //
    // }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws IOException
    {
        LOGGER.debug("Proxy received request for: " + request.getRequestLine().getUri());

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
            LOGGER.warn("Proxy request unrequestable (" + transaction.getRequestWrapper().getAbsoluteUriString() + "): " + e.toString(), e);
        }
        catch (InterruptedScanException e)
        {
            LOGGER.info("Scan aborted: " + e.toString(), e);
            return;
        }

        intercepted = checkResponseIntercept(transaction) || intercepted;
        if (testUninterceptedTransaction || intercepted && Scan.getScanSettings().isTestInterceptedRequests())
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
            for (Header header : transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
            {
                response.addHeader(HttpCloner.clone(header));
            }
            response.setStatusLine(HttpCloner.clone(transaction.getResponseWrapper().getStatusLine()));
        }
    }
}
