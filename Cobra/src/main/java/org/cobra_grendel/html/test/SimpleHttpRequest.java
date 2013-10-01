///*
// * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
// * 
// * This library is free software; you can redistribute it and/or modify it under
// * the terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation; either version 2.1 of the License, or (at your option)
// * any later version.
// * 
// * This library is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
// * details.
// * 
// * You should have received a copy of the GNU Lesser General Public License
// * along with this library; if not, write to the Free Software Foundation, Inc.,
// * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
// * 
// * Contact info: xamjadmin@users.sourceforge.net
// */
///*
// * Created on Nov 19, 2005
// */
//package org.cobra_grendel.html.test;
//
//import java.awt.Image;
//import java.awt.Toolkit;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.EventObject;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import org.cobra_grendel.html.HttpRequest;
//import org.cobra_grendel.html.ReadyStateChangeListener;
//import org.cobra_grendel.html.UserAgentContext;
//import org.cobra_grendel.util.EventDispatch;
//import org.cobra_grendel.util.GenericEventListener;
//import org.cobra_grendel.util.io.IORoutines;
//import org.w3c.dom.Document;
//
///**
// * The <code>SimpleHttpRequest</code> class implements the
// * {@link org.cobra_grendel.html.HttpRequest} interface. The
// * <code>HttpRequest</code> implementation provided by this class is simple,
// * with no caching. It creates a new thread for each new asynchronous request.
// * 
// * @author J. H. S.
// */
//public class SimpleHttpRequest implements HttpRequest
//{
//	private static final Logger logger = Logger.getLogger(SimpleHttpRequest.class.getName());
//	private java.net.URLConnection connection;
//	private final UserAgentContext context;
//	private final EventDispatch readyEvent = new EventDispatch();
//	private int readyState;
//	private byte[] responseBytes;
//	private String responseHeaders;
//	private java.util.Map responseHeadersMap;
//	
//	private int status;
//	
//	private String statusText;
//	
//	public SimpleHttpRequest(UserAgentContext context)
//	{
//		super();
//		this.context = context;
//	}
//	
//	public void abort()
//	{
//		URLConnection c;
//		synchronized (this)
//		{
//			c = connection;
//		}
//		if (c instanceof HttpURLConnection)
//		{
//			((HttpURLConnection) c).disconnect();
//		}
//		else if (c != null)
//		{
//			try
//			{
//				c.getInputStream().close();
//			}
//			catch (IOException ioe)
//			{
//				ioe.printStackTrace();
//			}
//		}
//	}
//	
//	public void addReadyStateChangeListener(final ReadyStateChangeListener listener)
//	{
//		readyEvent.addListener(new GenericEventListener()
//		{
//			public void processEvent(EventObject event)
//			{
//				listener.readyStateChanged();
//			}
//		});
//	}
//	
//	public synchronized String getAllResponseHeaders()
//	{
//		return responseHeaders;
//	}
//	
//	public synchronized int getReadyState()
//	{
//		return readyState;
//	}
//	
//	public synchronized byte[] getResponseBytes()
//	{
//		return responseBytes;
//	}
//	
//	public synchronized String getResponseHeader(String headerName)
//	{
//		Map headers = responseHeadersMap;
//		return headers == null ? null : (String) headers.get(headerName);
//	}
//	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.xamjwg.html.HttpRequest#getResponseImage()
//	 */
//	public synchronized Image getResponseImage()
//	{
//		byte[] bytes = responseBytes;
//		if (bytes == null)
//		{
//			return null;
//		}
//		return Toolkit.getDefaultToolkit().createImage(bytes);
//	}
//	
//	public synchronized String getResponseText()
//	{
//		byte[] bytes = responseBytes;
//		// TODO: proper charset
//		try
//		{
//			return bytes == null ? null : new String(bytes, "ISO-8859-1");
//		}
//		catch (UnsupportedEncodingException uee)
//		{
//			return null;
//		}
//	}
//	
//	public synchronized Document getResponseXML()
//	{
//		byte[] bytes = responseBytes;
//		if (bytes == null)
//		{
//			return null;
//		}
//		java.io.InputStream in = new ByteArrayInputStream(bytes);
//		try
//		{
//			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
//		}
//		catch (Exception err)
//		{
//			logger.log(Level.WARNING, "Unable to parse response as XML.", err);
//			return null;
//		}
//	}
//	
//	public synchronized int getStatus()
//	{
//		return status;
//	}
//	
//	public synchronized String getStatusText()
//	{
//		return statusText;
//	}
//	
//	public void open(final String method, final java.net.URL url, boolean asyncFlag, final String userName,
//	        final String password)
//	{
//		if (asyncFlag)
//		{
//			// Should use a thread pool instead
//			new Thread("Request")
//			{
//				@Override
//				public void run()
//				{
//					openSync(method, url, userName, password);
//				}
//			}.start();
//		}
//		else
//		{
//			openSync(method, url, userName, password);
//		}
//	}
//	
//	public void open(String method, String url)
//	{
//		this.open(method, url, true);
//	}
//	
//	public void open(String method, String url, boolean asyncFlag)
//	{
//		this.open(method, url, asyncFlag, null);
//	}
//	
//	public void open(String method, String url, boolean asyncFlag, String userName)
//	{
//		this.open(method, url, asyncFlag, userName, null);
//	}
//	
//	public void open(String method, String url, boolean asyncFlag, String userName, String password)
//	{
//		try
//		{
//			URL urlObj = new URL(url);
//			this.open(method, urlObj, asyncFlag, userName, password);
//		}
//		catch (MalformedURLException mfu)
//		{
//			logger.log(Level.WARNING, "Bad request URL:" + url, mfu);
//			changeState(HttpRequest.STATE_COMPLETE, 400, "Malformed URI", null);
//		}
//	}
//	
//	public void open(String method, URL url)
//	{
//		this.open(method, url, true, null, null);
//	}
//	
//	public void open(String method, URL url, boolean asyncFlag)
//	{
//		this.open(method, url, asyncFlag, null, null);
//	}
//	
//	private void changeState(int readyState, int status, String statusMessage, byte[] bytes)
//	{
//		synchronized (this)
//		{
//			this.readyState = readyState;
//			this.status = status;
//			statusText = statusMessage;
//			responseBytes = bytes;
//		}
//		readyEvent.fireEvent(null);
//	}
//	
//	private String getAllResponseHeaders(URLConnection c)
//	{
//		int idx = 0;
//		String value;
//		StringBuffer buf = new StringBuffer();
//		while ((value = c.getHeaderField(idx)) != null)
//		{
//			String key = c.getHeaderFieldKey(idx);
//			buf.append(key);
//			buf.append(": ");
//			buf.append(value);
//			idx++;
//		}
//		return buf.toString();
//	}
//	
//	protected void openSync(String method, java.net.URL url, String userName, String password)
//	{
//		try
//		{
//			abort();
//			URLConnection c = url.openConnection();
//			synchronized (this)
//			{
//				connection = c;
//			}
//			try
//			{
//				c.setRequestProperty("User-Agent", context.getUserAgent());
//				changeState(HttpRequest.STATE_LOADING, 0, "", null);
//				java.io.InputStream in = c.getInputStream();
//				int contentLength = c.getContentLength();
//				byte[] bytes = IORoutines.load(in, contentLength == -1 ? 4096 : contentLength);
//				int status = 0;
//				String statusText = "";
//				if (c instanceof HttpURLConnection)
//				{
//					HttpURLConnection hc = (HttpURLConnection) c;
//					status = hc.getResponseCode();
//					statusText = hc.getResponseMessage();
//				}
//				synchronized (this)
//				{
//					responseHeaders = this.getAllResponseHeaders(c);
//					responseHeadersMap = c.getHeaderFields();
//				}
//				changeState(HttpRequest.STATE_COMPLETE, status, statusText, bytes);
//			}
//			finally
//			{
//				synchronized (this)
//				{
//					connection = null;
//				}
//			}
//		}
//		catch (Exception err)
//		{
//			changeState(HttpRequest.STATE_COMPLETE, err instanceof java.io.FileNotFoundException ? 404 : 400, err
//			        .getMessage(), null);
//			logger.log(Level.WARNING, "Request failed on url=" + url, err);
//		}
//	}
//}
