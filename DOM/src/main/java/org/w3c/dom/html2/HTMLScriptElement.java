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
 * Copyright (c) 2003 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en Automatique,
 * Keio University). All Rights Reserved. This program is distributed under the
 * W3C's Software Intellectual Property License. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package org.w3c.dom.html2;

/**
 * Script statements. See the SCRIPT element definition in HTML 4.01.
 * <p>
 * See also the <a
 * href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document
 * Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLScriptElement extends HTMLElement
{
	/**
	 * The character encoding of the linked resource. See the charset attribute
	 * definition in HTML 4.01.
	 */
	public String getCharset();
	
	/**
	 * Indicates that the user agent can defer processing of the script. See the
	 * defer attribute definition in HTML 4.01.
	 */
	public boolean getDefer();
	
	/**
	 * Reserved for future use.
	 */
	public String getEvent();
	
	/**
	 * Reserved for future use.
	 */
	public String getHtmlFor();
	
	/**
	 * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]
	 * designating an external script. See the src attribute definition in HTML
	 * 4.01.
	 */
	public String getSrc();
	
	/**
	 * The script content of the element.
	 */
	public String getText();
	
	/**
	 * The content type of the script language. See the type attribute
	 * definition in HTML 4.01.
	 */
	public String getType();
	
	/**
	 * The character encoding of the linked resource. See the charset attribute
	 * definition in HTML 4.01.
	 */
	public void setCharset(String charset);
	
	/**
	 * Indicates that the user agent can defer processing of the script. See the
	 * defer attribute definition in HTML 4.01.
	 */
	public void setDefer(boolean defer);
	
	/**
	 * Reserved for future use.
	 */
	public void setEvent(String event);
	
	/**
	 * Reserved for future use.
	 */
	public void setHtmlFor(String htmlFor);
	
	/**
	 * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]
	 * designating an external script. See the src attribute definition in HTML
	 * 4.01.
	 */
	public void setSrc(String src);
	
	/**
	 * The script content of the element.
	 */
	public void setText(String text);
	
	/**
	 * The content type of the script language. See the type attribute
	 * definition in HTML 4.01.
	 */
	public void setType(String type);
	
}
