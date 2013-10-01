package com.grendelscan.requester.cobraIntegration;


import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.ReadyStateChangeListener;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.grendelscan.logging.Log;
import com.grendelscan.requester.RequestOptions;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.factories.UriFactory;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.transactions.UnrequestableTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.scan.Scan;

/**
 * This is used by HttpTransaction and should not be used by
 * itself unless you really know what you are doing. It may
 * go away in the future.
 * 
 * @author David Byrne
 */
public class CobraHttpRequest implements HttpRequest
{
	private final StandardHttpTransaction transaction;
	private final RequestOptions cobraRequestOptions;
	private transient final static String FAILED_RESPONSE = "HTTP/1.0 503 Cobra Request Failed\r\n\r\n"; 
	
	public CobraHttpRequest(int referingTransactionId, int testJobId)
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
		Log.warn("Abort not implemented in CobraHttpRequest");
	}

	@Override
	public void addReadyStateChangeListener(ReadyStateChangeListener arg0)
	{
		Log.warn("addReadyStateChangeListener not implemented in CobraHttpRequest");
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
	public String getResponseHeader(String headerName)
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
		Log.warn("getResponseImage not implemented in CobraHttpRequest");
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
			Log.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
		}
		catch (IOException e)
		{
			Log.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
		}
		catch (ParserConfigurationException e)
		{
			Log.warn("Couldn't parse Cobra response as XML: " + e.toString(), e);
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
	public void open(String method, String url)
	{
		try
		{
			open(method, UriFactory.makeUri(url, true));
		}
		catch (URISyntaxException e)
		{
			Log.error("Stupid URL/URI. I HATE YOU: " + e.toString(), e);
		}
		catch (InterruptedScanException e)
		{
			Log.info("Scan aborted: " + e.toString(), e);
			return;
		}
	}

	@Override
	public void open(String method, String url, boolean asyncFlag)
	{
		open(method, url);
	}

	@Override
	public void open(String method, String url, boolean asyncFlag, String userName)
	{
		open(method, url);
	}

	@Override
	public void open(String method, String url, boolean asyncFlag, String userName, String password)
	{
		open(method, url);
	}

	@Override
	public void open(String method, URL url)
	{
		try
		{
			open(method, url.toURI());
		}
		catch (URISyntaxException e)
		{
			Log.error("Stupid URL/URI. I HATE YOU: " + e.toString(), e);
		}
		catch (InterruptedScanException e)
		{
			Log.info("Scan aborted: " + e.toString(), e);
		}
	}
	
	public void open(String method, URI uri) throws InterruptedScanException
	{
		transaction.getRequestWrapper().setURI(uri.toASCIIString(), true);
		transaction.getRequestWrapper().setMethod(method);
		try
		{
			transaction.execute();
		}
		catch (UnrequestableTransaction e)
		{
			Log.warn("Cobra request couldn't be completed: " + e.toString(), e);
		}
		if (!transaction.isSuccessfullExecution())
		{
			
		}
	}

	@Override
	public void open(String method, URL url, boolean asyncFlag)
	{
		open(method, url);
	}
}
