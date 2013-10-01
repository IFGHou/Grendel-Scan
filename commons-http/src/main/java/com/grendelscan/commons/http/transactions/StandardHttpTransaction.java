/*
 * HttpRequest.java
 * 
 * Created on September 15, 2007, 2:34 PM
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package com.grendelscan.commons.http.transactions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.grendelscan.commons.FileUtils;
import com.grendelscan.commons.http.CookieJar;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.URIStringUtils;
import com.grendelscan.commons.http.apache_overrides.client.CustomHttpClient;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableBasicCookie;
import com.grendelscan.commons.http.cobra.CobraUserAgent;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;

/**
 * 
 * This class represents a complete HTTP transaction including the request and the response.
 * 
 * @author David Byrne
 * 
 * 
 */
public class StandardHttpTransaction extends HttpTransactionFields
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardHttpTransaction.class);

    private static final long serialVersionUID = 1L;

    /**
     * For deserialization
     */
    @SuppressWarnings("unused")
    private StandardHttpTransaction()
    {
        super();
    }

    public StandardHttpTransaction(final TransactionSource source, final int testJobId)
    {
        super(source, testJobId);
        setRequestOptions(new RequestOptions());
        Scan.getInstance().getTransactionRecord().addOrRefreshTransactionReference(this);
    }

    /**
     * If there is already a cookie with the same name, it will be replaced
     * 
     * @param cookie
     */
    public void addCookie(final Cookie cookie)
    {
        getCookieJar().addCookie(cookie);
    }

    public void addRawCookieHeader(final Header setCookie)
    {

        try
        {
            for (Cookie cookie : CookieJar.getCookieSpec().parse(setCookie, getCookieOrigin()))
            {
                addCookie(cookie);
            }
        }
        catch (MalformedCookieException e)
        {
            LOGGER.error("Problem with set-cookie header in AbstractHttpTransaction.addRawCookieHeader(): " + e.toString(), e);
        }

    }

    public synchronized void addSessionStateName(final String name)
    {
        getSessionStateNames().add(name);
    }

    protected void applyDataChanges()
    {
        if (transactionContainer != null)
        {
            if (transactionContainer.getBodyData() != null)
            {
                getRequestWrapper().setBody(DataUtils.getBytes(transactionContainer.getBodyData()));
            }
            if (transactionContainer.getUrlQueryDataContainer() != null)
            {
                String baseUri = URIStringUtils.getFileUri(getRequestWrapper().getURI());
                getRequestWrapper().setURI(baseUri + "?" + new String(DataUtils.getBytes(transactionContainer.getUrlQueryDataContainer())), false);
            }
        }
    }

    private synchronized void checkRequestThrottle()
    {

        if (getSource() == TransactionSource.PROXY || getSource() == TransactionSource.MANUAL_REQUEST)
        {
            return;
        }

        boolean run = false;
        while (!run)
        {
            long second = (long) ((double) new Date().getTime() / (double) 1000);
            if (second > getCurrentSecond())
            {
                setCurrentSecond(second);
                setPerSecondTransactionCount(1);
                run = true;
            }
            else if (getPerSecondTransactionCount() < Scan.getScanSettings().getMaxRequestsPerSecond())
            {
                setPerSecondTransactionCount(getPerSecondTransactionCount() + 1);
                run = true;
            }
            if (!run)
            {
                try
                {
                    // synchronized (this)
                    {
                        Thread.sleep(250);
                    }
                }
                catch (InterruptedException e)
                {
                    // Probably a stop; this will be handled elsewhere
                    break;
                }
            }
        }
    }

    public StandardHttpTransaction cloneForReferer(final TransactionSource source, final int testJobId)
    {
        StandardHttpTransaction target = new StandardHttpTransaction(source, testJobId);
        super.cloneForReferer(target);
        target.getRequestWrapper().getHeaders().clearHeaders();
        target.getRequestWrapper().getHeaders().addHeader(HttpHeaders.REFERER, getRequestWrapper().getAbsoluteUriString());
        target.setCookieJar(getChildrensCookieJar());
        return target;
    }

    public StandardHttpTransaction cloneForSessionReuse(final TransactionSource source, final int testJobId)
    {
        StandardHttpTransaction target = new StandardHttpTransaction(source, testJobId);
        target.setCookieJar(getChildrensCookieJar());
        super.cloneForSessionReuse(target);
        return target;
    }

    public StandardHttpTransaction cloneFullRequest(final TransactionSource source, final int testJobId)
    {
        StandardHttpTransaction target = new StandardHttpTransaction(source, testJobId);
        super.cloneFullRequest(target);
        return target;
    }

    /**
     * Executes the transaction using the provided {@link HttpClient} object. The {@link HttpState} is taken from the client. Nothing is returned since the HTTP response is also stored inside of this
     * class. There is some internal logic here, so this should be the only way that a request is executed.
     * 
     * @param client
     * @throws InterruptedScanException
     */
    @SuppressWarnings("unused")
    public final void execute() throws UnrequestableTransaction, InterruptedScanException
    {
        if (isSuccessfullExecution())
        {
            IllegalStateException e = new IllegalStateException("This transaction has already been executed");
            LOGGER.error(e.toString(), e);
            throw e;
        }

        applyDataChanges();

        // save();
        LOGGER.debug(getSource().toString() + " (" + getRequestOptions().reason + ") is requesting " + getRequestWrapper().getMethod() + " " + getRequestWrapper().getAbsoluteUriString());
        Scan.getInstance().getRequesterQueue().isRequestable(this);
        if (getRequestOptions().validateUriFormat)
        {
            try
            {
                new URI(getRequestWrapper().getURI());
                new URI(getRequestWrapper().getAbsoluteUriString());
            }
            catch (URISyntaxException e)
            {
                unrequestable = true;
                throw new UnrequestableTransaction("Invalid URI", e);
            }
        }

        if (getRequestOptions().handleSessions)
        {
            SessionStates.getInstance().identifySessions(this);
            updateCookies();
        }

        checkRequestThrottle();

        try
        {
            setRequestSentTime(new java.util.Date().getTime());
            if (getRequestOptions().useCache)
            {
                getClient().cacheExecute(this);
            }
            else
            {
                getClient().customExecute(this);
            }
            setResponseRecievedTime(new java.util.Date().getTime());

            // Must be handled before the redirects are
            if (getRequestOptions().handleSessions)
            {
                SessionStates.getInstance().postExecutionFollowup(this);
            }

            int statusCode = getResponseWrapper().getStatusLine().getStatusCode();
            if (getRequestOptions().followRedirects && HttpUtils.isRedirectCode(statusCode))
            {
                handleRedirect();
            }
        }
        catch (HttpException e)
        {
            // error in executing we just need to cleanup now
            logFailedExecution();
            return;
        }
        finally
        {
            save();
        }

        setSuccessfullExecution(true);

        logSuccessfullExecution();
        if (getRequestOptions().testTransaction)
        {
            Scan.getInstance().getCategorizerQueue().addTransaction(this);
        }
    }

    public final Collection<StandardHttpTransaction> getAllRedirectChildren()
    {
        List<StandardHttpTransaction> transactions = new ArrayList<StandardHttpTransaction>(1);
        StandardHttpTransaction transaction = this;
        while (transaction.hasRedirectResponse())
        {
            transaction = Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRedirectChildId());
            transactions.add(transaction);
        }
        return transactions;
    }

    protected CustomHttpClient getClient()
    {
        return Scan.getInstance().getHttpClient();
    }

    /**
     * Only returns cookies that were actually used in this transaction
     * 
     * @param cookieName
     * @return
     */
    public SerializableBasicCookie getCookie(final String cookieName)
    {
        for (SerializableBasicCookie cookie : getUsedCookies())
        {
            if (cookie.getName().equals(cookieName))
            {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 
     * @return The filename of the URI, or an empty string if there isn't one
     */

    @Override
    public CookieOrigin getCookieOrigin()
    {
        if (cookieOrigin == null)
        {
            cookieOrigin = new CookieOrigin(getRequestWrapper().getHost(), getRequestWrapper().getNetworkPort(), getRequestWrapper().getPath(), getRequestWrapper().isSecure());
        }
        return cookieOrigin;
    }

    /**
     * This will take the ResponseCodeOverrides into account and return what the web server meant, not what the web server said.
     * 
     * @return
     * @throws InterruptedScanException
     * @throws InterruptedException
     */
    public int getLogicalResponseCode() throws InterruptedScanException
    {
        return getLogicalResponseCode(true);
    }

    /**
     * This will take the ResponseCodeOverrides into account and return what the web server meant, not what the web server said.
     * 
     * @return
     * @throws InterruptedScanException
     * @throws InterruptedException
     */
    private int getLogicalResponseCode(final boolean generateNewProfiles) throws InterruptedScanException
    {
        if (logicalResponseCode == 0 && getResponseWrapper() != null)
        {
            logicalResponseCode = Scan.getInstance().getResponseCodeOverrides().getLogicalResponseCode(this, generateNewProfiles, getTestJobId());
        }
        return logicalResponseCode;
    }

    /**
     * This will take the ResponseCodeOverrides into account and return what the web server meant, not what the web server said.
     * 
     * @return
     * @throws InterruptedScanException
     */
    public int getLogicalResponseCodeWithoutProfileGeneration() throws InterruptedScanException
    {
        return getLogicalResponseCode(false);
    }

    public String getSavedUrl()
    {
        return "./" + Scan.getScanSettings().getSavedTextTransactionsDirectory() + "/" + getId() + ".txt";
    }

    /**
     * Returns the response wrapper after all redirects are processed
     * 
     * @return
     */
    public HttpResponseWrapper getUltimateResponseWrapper()
    {
        StandardHttpTransaction transaction = this;
        while (transaction.hasRedirectResponse())
        {
            transaction = Scan.getInstance().getTransactionRecord().getTransaction(transaction.getRedirectChildId());
        }
        return transaction.getResponseWrapper();
    }

    /**
     * /** This will return a set of cookies that were/would be used in the request
     * 
     * @return
     */
    public Set<String> getUsedCookieNames()
    {
        Set<String> cookies = new HashSet<String>();
        for (Cookie cookie : getUsedCookies())
        {
            cookies.add(cookie.getName());
        }
        return cookies;
    }

    /**
     * This will return a set of cookies that were/would be used in the request
     * 
     * @return
     */
    public Set<SerializableBasicCookie> getUsedCookies()
    {
        Set<SerializableBasicCookie> cookies = new HashSet<SerializableBasicCookie>();
        for (SerializableBasicCookie cookie : getCookieJar().getCookies())
        {
            if (CookieJar.getCookieSpec().match(cookie, getCookieOrigin()))
            {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    private void handleRedirect() throws InterruptedScanException
    {
        if (getRedirectCount() > Scan.getScanSettings().getMaxRedirects())
        {
            LOGGER.info("Request for " + getRequestWrapper().getAbsoluteUriString() + " returned a redirect, but we are at max redirects.");
            return;
        }

        if (getResponseWrapper().getHeaders().getFirstHeader(HttpHeaders.LOCATION) == null)
        {
            LOGGER.warn("The request for " + getRequestWrapper().getAbsoluteUriString() + " returned a " + getResponseWrapper().getStatusLine().getStatusCode() + " redirect code, but no location header value");
            return;
        }

        String locationURI = getResponseWrapper().getHeaders().getFirstHeader(HttpHeaders.LOCATION).getValue();
        boolean absolute;
        try
        {
            absolute = URIStringUtils.isAbsolute(locationURI);
        }
        catch (URISyntaxException e2)
        {
            LOGGER.warn("The request for " + getRequestWrapper().getAbsoluteUriString() + " returned a " + getResponseWrapper().getStatusLine().getStatusCode() + " redirect code, but the location header value (" + locationURI + ") doesn't"
                            + " appear to be a valid URI: " + e2.getMessage());
            return;
        }

        if (!absolute)
        {
            try
            {
                URL location = new URL(new URL(getRequestWrapper().getAbsoluteUriString()), locationURI);
                locationURI = location.toExternalForm();
                // if (locationURI.startsWith("/"))
                // {
                // locationURI = URIStringUtils.getHostUriWithoutTrailingSlash(getRequestWrapper().getAbsoluteUriString()) + locationURI;
                // }
                // else
                // {
                // locationURI = URIStringUtils.getDirectoryUri(getRequestWrapper().getAbsoluteUriString()) + locationURI;
                // }
            }
            catch (MalformedURLException e)
            {
                LOGGER.error("Very, very weird problem getting the location header absolute URL", e);
            }
        }

        if (locationURI.equals(getRequestWrapper().getAbsoluteUriString()))
        {
            LOGGER.warn("The request for " + getRequestWrapper().getAbsoluteUriString() + " returned a " + getResponseWrapper().getStatusLine().getStatusCode() + " redirect code with a location header pointing at the original response.");
            return;
        }

        if (!Scan.getScanSettings().getUrlFilters().isUriAllowed(locationURI))
        {
            LOGGER.warn("The request for " + getRequestWrapper().getAbsoluteUriString() + " returned a " + getResponseWrapper().getStatusLine().getStatusCode() + " redirect code with the location header pointing to " + locationURI
                            + ". This URL is not allowed by the scan configuration.");
            return;
        }

        RequestOptions redirectOptions = getRequestOptions().clone();
        redirectOptions.reason = "Redirect for " + getRequestOptions().reason;
        redirectOptions.testTransaction = getRequestOptions().testRedirectTransactions;
        StandardHttpTransaction redirectTransaction = cloneForReferer(getSource(), getTestJobId());
        redirectTransaction.getRequestWrapper().getHeaders().removeHeaders(HttpHeaders.CONTENT_TYPE);
        redirectTransaction.setRedirectCount(getRedirectCount() + 1);
        redirectTransaction.setRequestOptions(redirectOptions);
        if (!absolute)
        {
            redirectTransaction.getRequestWrapper().copyNetworkTarget(getRequestWrapper());
            redirectTransaction.getRequestWrapper().getHeaders().addHeader(HttpHeaders.HOST, getRequestWrapper().getHost());
        }
        redirectTransaction.getRequestWrapper().setURI(locationURI, true);

        try
        {
            redirectTransaction.execute();
            setRedirectChildId(redirectTransaction.getId());
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn("Redirect request was unrequestable, but request was attempted. Odd: " + e.toString(), e);
        }
    }

    public boolean hasRedirectResponse()
    {
        return getRedirectChildId() > 0;
    }

    public boolean isResponsePresent()
    {
        if (getResponseWrapper() == null)
        {
            return false;
        }
        return true;
    }

    private void logFailedExecution()
    {
        Scan.getInstance().getTransactionRecord().incFailureCount(getRequestWrapper().getHost() + getRequestWrapper().getNetworkPort());
    }

    private void logSuccessfullExecution()
    {
        Scan.getInstance().getTransactionRecord().incSource(getRequestOptions().reason);
        incTotalExecutions();
    }

    /**
     * This will only remove cookies that match the cookie name and the host of the request
     * 
     * @param cookieName
     */
    public void removeCookie(final String cookieName)
    {
        for (Cookie cookie : getUsedCookies())
        {
            if (cookie.getName().equals(cookieName))
            {
                getCookieJar().removeCookie(cookie);
            }
        }
    }

    /**
     * This will parse the DOM with a renderer context and return the document. Mostly for XSS testing.
     */
    public Document runDOM()
    {
        if (getResponseWrapper().getBody() == null)
        {
            return null;
        }

        CobraUserAgent ua = new CobraUserAgent(getId(), true);
        SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext();
        DocumentBuilderImpl dbi = new DocumentBuilderImpl(ua, rcontext);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(getResponseWrapper().getBody());
        InputSource inputSource = new InputSourceImpl(inputStream, getRequestWrapper().getAbsoluteUriString(), getRequestWrapper().getHeaders().getCharacterSet());
        Document tmpDocument = null;
        try
        {
            tmpDocument = dbi.parse(inputSource, getId());
        }
        catch (SAXException e)
        {
            LOGGER.error(e.toString(), e);
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error(e.toString(), e);
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString(), e);
        }
        return tmpDocument;
    }

    public void save()
    {
        Scan.getInstance().getTransactionRecord().saveOrUpdateTransaction(this);
    }

    public void setResponseWrapper(final HttpResponseWrapper wrapper)
    {
        responseWrapper = wrapper;
        setSuccessfullExecution(true);
    }

    @Override
    public String toString()
    {
        return getRequestWrapper().toString() + "\r\n\r\n" + (getResponseWrapper() == null ? "" : getResponseWrapper().toString());
    }

    protected void updateCookies()
    {
        getRequestWrapper().getHeaders().removeHeaders("Cookie");
        if (getCookieJar().getCookies().size() > 0)
        {
            String cookieString = "";
            for (Cookie cookie : getCookieJar().getMatchingCookies(getCookieOrigin()))
            {
                cookieString += cookie.getName() + "=" + cookie.getValue() + "; ";
            }
            getRequestWrapper().getHeaders().addHeader("Cookie", cookieString);
        }
    }

    public synchronized void writeToDisk()
    {
        writeToDisk(false);
    }

    public synchronized void writeToDisk(final boolean force)
    {
        if (isSuccessfullExecution() && (!isWrittenToDisk() || force))
        {
            setWrittenToDisk(true);
            String filename = Scan.getInstance().getOutputDirectory() + File.separator + Scan.getScanSettings().getSavedTextTransactionsDirectory() + File.separator + getId() + ".txt";
            String text = toString();
            FileUtils.writeToFile(filename, text);
        }
    }

    // public List<QueryParameter> getAllQueryParameters()
    // {
    // List<QueryParameter> params = new ArrayList<QueryParameter>();
    // for (StandardQueryPayload payload : PayloadParsers.getAllQueryPayloads(this))
    // {
    // params.addAll(payload.getParameters());
    // }
    // return params;
    // }
    //
    // public List<PayloadComponent> getAllPayloadComponents()
    // {
    // List<PayloadComponent> params = new ArrayList<PayloadComponent>();
    // for (AbstractDataContainer<?> payload : PayloadParsers.getAllPayloads(this))
    // {
    // params.addAll(payload.getComponents());
    // }
    // return params;
    // }

    // public AbstractDataContainer<?> getPrimaryPayload()
    // {
    // List<AbstractDataContainer<?>> payloads = PayloadParsers.getAllPayloads(this);
    // AbstractDataContainer<?> bestChoice = null;
    // for (AbstractDataContainer<?> payload : payloads)
    // {
    // if (payload instanceof UrlEncodedPostPayload)
    // {
    // // always use a post payload
    // return payload;
    // }
    //
    // if (payload instanceof UrlEncodedQueryPayload)
    // {
    // // default to a URL query string
    // bestChoice = payload;
    // }
    // }
    //
    // if (bestChoice == null && payloads.size() > 0)
    // {
    // return payloads.get(0);
    // }
    //
    // return bestChoice;
    // }

    // public StandardQueryPayload getPrimaryQueryPayload()
    // {
    // List<StandardQueryPayload> payloads = PayloadParsers.getAllQueryPayloads(this);
    // StandardQueryPayload bestChoice = null;
    // for (StandardQueryPayload payload : payloads)
    // {
    // if (payload instanceof UrlEncodedPostPayload)
    // {
    // // always use a post payload
    // return payload;
    // }
    //
    // if (payload instanceof UrlEncodedQueryPayload)
    // {
    // // default to a URL query string
    // bestChoice = payload;
    // }
    // }
    //
    // if (bestChoice == null && payloads.size() > 0)
    // {
    // return payloads.get(0);
    // }
    //
    // return bestChoice;
    // }

    // public StandardQueryPayload getOrCreatePrimaryQueryPayload()
    // {
    // StandardQueryPayload payload = getPrimaryQueryPayload();
    // if (payload == null)
    // {
    // if (getRequestWrapper().getMethod().equals(HttpPost.METHOD_NAME))
    // {
    // payload = new UrlEncodedPostPayload(getId());
    // }
    // else
    // {
    // payload = new UriQueryPayload(getId());
    // }
    // }
    // return payload;
    // }
}
