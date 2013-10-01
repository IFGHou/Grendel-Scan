package com.grendelscan.commons.http.wrappers;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.grendelscan.commons.MimeUtils;
import com.grendelscan.commons.http.HttpConstants;
import com.grendelscan.commons.http.HttpUtils;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableStatusLine;
import com.grendelscan.commons.http.cobra.CobraUserAgent;

public class HttpResponseWrapper extends HttpMessageWrapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseWrapper.class);
    private static final long serialVersionUID = 1L;
    private SerializableStatusLine statusLine;
    protected transient Document dom;
    private String strippedResponseText;

    private static transient long domParsingTime = 0;

    public static final long getDomParsingTime()
    {
        return domParsingTime;
    }

    public static final void setDomParsingTime(final long domParsingTime)
    {
        HttpResponseWrapper.domParsingTime = domParsingTime;
    }

    protected HttpResponseWrapper()
    {
        super();
    }

    public HttpResponseWrapper(final int transactionId)
    {
        super(transactionId);
    }

    public HttpResponseWrapper(final int transactionId, final HttpResponse response)
    {
        super(transactionId);
        setResponse(response);
    }

    private Document generateDOM()
    {
        Document dom = null;
        if (getBody() != null && MimeUtils.isHtmlMimeType(getHeaders().getMimeType()))
        {
            Date t = new Date();
            CobraUserAgent ua = new CobraUserAgent(transactionId, true);
            DocumentBuilderImpl dbi = new DocumentBuilderImpl(ua);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(getBody());
            try
            {
                InputSource inputSource = new InputSourceImpl(inputStream, Scan.getInstance().getTransactionRecord().getTransaction(transactionId).getRequestWrapper().getAbsoluteUriString(), getHeaders().getCharacterSet());
                HTMLDocumentImpl document = (HTMLDocumentImpl) dbi.createDocument(inputSource, transactionId);
                document.load();
                dom = document;
            }
            catch (SAXException e)
            {
                LOGGER.error(e.toString(), e);
                e.printStackTrace();
            }
            catch (FileNotFoundException e)
            {
                LOGGER.error(e.toString(), e);
                e.printStackTrace();
            }
            catch (IOException e)
            {
                LOGGER.error(e.toString(), e);
                e.printStackTrace();
            }
            Date t2 = new Date();
            setDomParsingTime(getDomParsingTime() + t2.getTime() - t.getTime());
        }
        return dom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.wrappers.HttpMessageWrapper#getBytes()
     */
    @Override
    public byte[] getBytes()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            out.write(statusLine.toString().getBytes());
            out.write(HttpConstants.CRLF_BYTES);
            out.write(super.getBytes());
        }
        catch (IOException e)
        {
            LOGGER.error("Weird problem getting bytes from response wrapper: " + e.toString(), e);
        }

        return out.toByteArray();
    }

    public synchronized Document getResponseDOM()
    {
        if (dom != null)
        {
            return dom;
        }
        dom = generateDOM();
        return dom;
    }

    public Document getResponseDOMClone()
    {
        return generateDOM();
    }

    public StatusLine getStatusLine()
    {
        return statusLine;
    }

    public final String getStrippedResponseText()
    {
        if (strippedResponseText == null)
        {
            updateStrippedResponseText();
        }
        return strippedResponseText;
    }

    private void setResponse(final HttpResponse response)
    {
        HttpEntity entity = response.getEntity();

        // preps an empty response body, incase it was empty

        if (entity != null)
        {
            try
            {

                body = HttpUtils.entityToByteArray(entity, Scan.getScanSettings().getMaxFileSizeKiloBytes());
                if (body == null)
                {
                    body = new byte[0];
                }
            }
            catch (IOException e)
            {
                LOGGER.error("Problem processing HTTP response: " + e.toString(), e);
            }
        }

        getHeaders().addHeaders(response.getAllHeaders());
        statusLine = new SerializableStatusLine(response.getStatusLine());
    }

    public final void setStatusLine(final SerializableStatusLine statusLine)
    {
        this.statusLine = statusLine;
    }

    public final void setStrippedResponseText(final String strippedResponseText)
    {
        this.strippedResponseText = strippedResponseText;
    }

    @Override
    public String toString()
    {
        return new String(getBytes());
    }

    protected void updateStrippedResponseText()
    {
        String type = getHeaders().getMimeType();
        if (MimeUtils.isHtmlMimeType(type))
        {
            long start = new Date().getTime();
            setStrippedResponseText(HtmlNodeWriter.writeTextOnly(getResponseDOM(), false));
            long end = new Date().getTime();
            Scan.getInstance().incrementDomTime(end - start);
        }
        else if (MimeUtils.isWebTextMimeType(type) && getBody() != null)
        {
            setStrippedResponseText(new String(getBody()));
        }
        if (strippedResponseText == null)
        {
            setStrippedResponseText("");
        }
    }

}
