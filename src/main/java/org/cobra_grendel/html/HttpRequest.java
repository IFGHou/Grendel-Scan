/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Nov 13, 2005
 */
package org.cobra_grendel.html;

import java.awt.Image;
import java.net.URL;

import org.w3c.dom.Document;

/**
 * The <code>HttpRequest</code> interface should be implemented to provide web
 * request capabilities.
 */
public interface HttpRequest
{
	/**
	 * The complete request state.
	 */
	public static final int STATE_COMPLETE = 4;
	
	/**
	 * The interactive request state.
	 */
	public static final int STATE_INTERACTIVE = 3;
	
	/**
	 * The loaded request state.
	 */
	public static final int STATE_LOADED = 2;
	
	/**
	 * The loading request state.
	 */
	public static final int STATE_LOADING = 1;
	
	/**
	 * The uninitialized request state.
	 */
	public static final int STATE_UNINITIALIZED = 0;
	
	/**
	 * Aborts an ongoing request.
	 */
	public void abort();
	
	/**
	 * Adds a listener of ReadyState changes. The listener should be invoked
	 * even in the case of errors.
	 * 
	 * @param listener
	 *            An instanceof of
	 *            {@link org.cobra_grendel.html.ReadyStateChangeListener}
	 */
	public void addReadyStateChangeListener(ReadyStateChangeListener listener);
	
	/**
	 * Gets a string with all the response headers.
	 */
	public String getAllResponseHeaders();
	
	/**
	 * Gets the state of the request, a value between 0 and 4.
	 * 
	 * @return A value corresponding to one of the STATE* constants in this
	 *         class.
	 */
	public int getReadyState();
	
	/**
	 * Gets the request response bytes.
	 */
	public byte[] getResponseBytes();
	
	/**
	 * Gets a response header value.
	 * 
	 * @param headerName
	 *            The name of the header.
	 */
	public String getResponseHeader(String headerName);
	
	/**
	 * Gets the request response as an AWT image.
	 */
	public Image getResponseImage();
	
	/**
	 * Gets the request response as text.
	 */
	public String getResponseText();
	
	/**
	 * Gets the request response as an XML DOM.
	 */
	public Document getResponseXML();
	
	/**
	 * Gets the status of the response. Note that this can be 0 for file
	 * requests in addition to 200 for successful HTTP requests.
	 */
	public int getStatus();
	
	/**
	 * Gets the status text of the request, e.g. "OK" for 200.
	 */
	public String getStatusText();
	
	/**
	 * Starts an asynchronous request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 */
	public void open(String method, String url);
	
	/**
	 * Opens a request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 * @param asyncFlag
	 *            Whether the request should be asynchronous.
	 */
	public void open(String method, String url, boolean asyncFlag);
	
	/**
	 * Opens a request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 * @param asyncFlag
	 *            Whether the request should be asynchronous.
	 * @param userName
	 *            The HTTP authentication user name.
	 */
	public void open(String method, String url, boolean asyncFlag, String userName);
	
	/**
	 * Opens a request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 * @param asyncFlag
	 *            Whether the request should be asynchronous.
	 * @param userName
	 *            The HTTP authentication user name.
	 * @param password
	 *            The HTTP authentication password.
	 */
	public void open(String method, String url, boolean asyncFlag, String userName, String password);
	
	/**
	 * Opens an asynchronous request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 */
	public void open(String method, URL url);
	
	/**
	 * Opens an request.
	 * 
	 * @param method
	 *            The request method.
	 * @param url
	 *            The destination URL.
	 * @param asyncFlag
	 *            Whether the request is asynchronous.
	 */
	public void open(String method, URL url, boolean asyncFlag);
}
