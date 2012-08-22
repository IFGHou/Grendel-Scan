package com.grendelscan.requester.http.wrappers;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.html.parser.DocumentBuilderImpl;
import org.cobra_grendel.html.parser.InputSourceImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.grendelscan.html.HtmlNodeWriter;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.cobraIntegration.CobraUserAgent;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.apache_overrides.serializable.SerializableStatusLine;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.HttpUtils;
import com.grendelscan.utils.MimeUtils;

public class HttpResponseWrapper extends HttpMessageWrapper
{
	private static final long	serialVersionUID	= 1L;
	private SerializableStatusLine statusLine;
	protected transient Document 					dom;
	private String strippedResponseText; 

	private static transient long						domParsingTime					= 0;
	public static final long getDomParsingTime()
	{
		return domParsingTime;
	}

	public static final void setDomParsingTime(long domParsingTime)
	{
		HttpResponseWrapper.domParsingTime = domParsingTime;
	}
	
	protected HttpResponseWrapper()
	{
		super();
	}
	
	public HttpResponseWrapper(int transactionId, HttpResponse response)
    {
		super(transactionId);
	    setResponse(response);
    }

	public HttpResponseWrapper(int transactionId)
    {
		super(transactionId);
    }


	private void setResponse(HttpResponse response)
    {
    	HttpEntity entity = response.getEntity();
    	
    	// preps an empty response body, incase it was empty
    	
    	if (entity != null)
    	{
	    	try
	    	{
	    		
	    		body = HttpUtils.entityToByteArray(entity, Scan.getScanSettings().getMaxFileSizeKiloBytes());
	    		if (body == null)
	    			body = new byte[0];
	    	}
	    	catch (IOException e) 
			{
				Log.error("Problem processing HTTP response: " + e.toString(), e);
			}
    	}
    	
    	getHeaders().addHeaders(response.getAllHeaders());
    	statusLine = new SerializableStatusLine(response.getStatusLine());
    }


	public StatusLine getStatusLine()
    {
    	return statusLine;
    }



	@Override
	public String toString()
	{
		return new String(getBytes()); 
	}


	/* (non-Javadoc)
	 * @see com.grendelscan.requester.http.wrappers.HttpMessageWrapper#getBytes()
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
			Log.error("Weird problem getting bytes from response wrapper: " + e.toString(), e);
		}
		
		return out.toByteArray();
	}

	public final void setStatusLine(SerializableStatusLine statusLine)
	{
		this.statusLine = statusLine;
	}
	
	private Document generateDOM()
	{
		Document dom = null;
		if (getBody() != null && MimeUtils.isHtmlMimeType(getHeaders().getMimeType()))
		{
			Date t = new Date();
			CobraUserAgent ua = new CobraUserAgent(true);
			DocumentBuilderImpl dbi = new DocumentBuilderImpl(ua);

			ByteArrayInputStream inputStream = new ByteArrayInputStream(getBody());
			try
			{
				InputSource inputSource =
						new InputSourceImpl(inputStream, 
								Scan.getInstance().getTransactionRecord().getTransaction(transactionId).getRequestWrapper().getAbsoluteUriString(),
								getHeaders().getCharacterSet());
				HTMLDocumentImpl document = (HTMLDocumentImpl) dbi.createDocument(inputSource, transactionId);
				document.load();
				dom = document;
			}
			catch (SAXException e)
			{
				Log.error(e.toString(), e);
				e.printStackTrace();
			}
			catch (FileNotFoundException e)
			{
				Log.error(e.toString(), e);
				e.printStackTrace();
			}
			catch (IOException e)
			{
				Log.error(e.toString(), e);
				e.printStackTrace();
			}
			Date t2 = new Date();
			setDomParsingTime(getDomParsingTime() + t2.getTime() - t.getTime());
		}
		return dom;
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

	public final String getStrippedResponseText()
	{
		if(strippedResponseText == null)
		{
			updateStrippedResponseText();
		}
		return strippedResponseText;
	}

	public final void setStrippedResponseText(String strippedResponseText)
	{
		this.strippedResponseText = strippedResponseText;
	}
	
	

	protected void updateStrippedResponseText()
	{
		String type = getHeaders().getMimeType();
		if (MimeUtils.isHtmlMimeType(type))
		{
			long start = (new Date()).getTime();
			setStrippedResponseText(HtmlNodeWriter.writeTextOnly(getResponseDOM(), false));
			long end = (new Date()).getTime();
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
