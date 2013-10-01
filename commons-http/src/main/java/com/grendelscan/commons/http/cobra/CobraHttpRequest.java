package com.grendelscan.commons.http.cobra;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.grendelscan.commons.http.RequestOptions;
import com.grendelscan.commons.http.factories.UriFactory;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.transactions.UnrequestableTransaction;

/**
 * This is used by HttpTransaction and should not be used by itself unless you really know what you are doing. It may go away in the future.
 * 
 * @author David Byrne
 */
public class CobraHttpRequest implements HttpRequest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CobraHttpRequest.class);
    private final StandardHttpTransaction transaction;
    private final RequestOptions cobraRequestOptions;
    private transient final static String FAILED_RESPONSE = "HTTP/1.0 503 Cobra Request Failed\r\n\r\n";

    public CobraHttpRequest(final int referingTransactionId, final int testJobId)
    {
        cobraRequestOptions = new RequestOptions();
        cobraRequestOptions.testTransaction = true;
        cobraRequestOptions.useCache = true;
        cobraRequestOptions.reason = "CobraHttpRequest";
        transaction = Scan.getInstance().getTransactionRecord().getTransaction(referingTransactionId).cloneForReferer(TransactionSource.COBRA, testJobId);
        transaction.setRequestOptions(cobraRequestOptions);
    }

    @Override
    public void abort()
    {
        LOGGER.warn("Abort not implemented in CobraHttpRequest");
    }

    @Override
    public void addReadyStateChangeListener(final ReadyStateChangeListener arg0)
    {
        LOGGER.warn("addReadyStateChangeListener not implemented in CobraHttpRequest");
    }

    @Override
    public String getAllResponseHeaders()
    {
        if (!transaction.isSuccessfullExecution())
        {
            return "";
        }
        return transaction.getUltimateResponseWrapper().getHeaders().toString();
    }

    @Override
    public int getReadyState()
    {
        if (transaction == null)
        {
            return HttpRequest.STATE_UNINITIALIZED;
        }

        if (transaction.isResponsePresent())
        {
            return HttpRequest.STATE_COMPLETE;
        }

        return HttpRequest.STATE_LOADING;
    }

    @Override
    public byte[] getResponseBytes()
    {
        if (!transaction.isSuccessfullExecution())
        {
            return FAILED_RESPONSE.getBytes();
        }
        return transaction.getUltimateResponseWrapper().getBytes();
    }

    @Override
    public String getResponseHeader(final String headerName)
    {
        if (!transaction.isSuccessfullExecution())
        {
            return "";
        }
        return transaction.getUltimateResponseWrapper().getHeaders().getFirstHeader(headerName).getValue();
    }

    @Override
    public Image getResponseImage()
    {
        LOGGER.warn("getResponseImage not implemented in CobraHttpRequest");
        return null;
    }

    @Override
    public String getResponseText()
    {
        if (!transaction.isSuccessfullExecution())
        {
            return "";
        }
        return new String(transaction.getUltimateResponseWrapper().getBody());
    }

    @Override
    public Document getResponseXML()
    {
        java.io.InputStream in = new ByteArrayInputStream(transaction.getUltimateResponseWrapper().getBody());
        try
        {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }
        catch (SAXException e)
        {
            LOGGER.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
        }
        catch (IOException e)
        {
            LOGGER.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
        }
        catch (ParserConfigurationException e)
        {
            LOGGER.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
        }
        return null;
    }

    @Override
    public int getStatus()
    {
        if (!transaction.isSuccessfullExecution())
        {
            return 503;
        }

        return transaction.getUltimateResponseWrapper().getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText()
    {
        if (!transaction.isSuccessfullExecution())
        {
            return "Cobra Request Failed";
        }
        return transaction.getUltimateResponseWrapper().getStatusLine().getReasonPhrase();
    }

    @Override
    public void open(final String method, final String url)
    {
        try
        {
            open(method, UriFactory.makeUri(url, true));
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Stupid URL/URI. I HATE YOU: " + e.toString(), e);
        }
        catch (InterruptedScanException e)
        {
            LOGGER.info("Scan aborted: " + e.toString(), e);
            return;
        }
    }

    @Override
    public void open(final String method, final String url, final boolean asyncFlag)
    {
        open(method, url);
    }

    @Override
    public void open(final String method, final String url, final boolean asyncFlag, final String userName)
    {
        open(method, url);
    }

    @Override
    public void open(final String method, final String url, final boolean asyncFlag, final String userName, final String password)
    {
        open(method, url);
    }

    public void open(final String method, final URI uri) throws InterruptedScanException
    {
        transaction.getRequestWrapper().setURI(uri.toASCIIString(), true);
        transaction.getRequestWrapper().setMethod(method);
        try
        {
            transaction.execute();
        }
        catch (UnrequestableTransaction e)
        {
            LOGGER.warn("Cobra request couldn't be completed: " + e.toString(), e);
        }
        if (!transaction.isSuccessfullExecution())
        {

        }
    }

    @Override
    public void open(final String method, final URL url)
    {
        try
        {
            open(method, url.toURI());
        }
        catch (URISyntaxException e)
        {
            LOGGER.error("Stupid URL/URI. I HATE YOU: " + e.toString(), e);
        }
        catch (InterruptedScanException e)
        {
            LOGGER.info("Scan aborted: " + e.toString(), e);
        }
    }

    @Override
    public void open(final String method, final URL url, final boolean asyncFlag)
    {
        open(method, url);
    }
}
