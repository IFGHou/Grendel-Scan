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
 * Created on Oct 22, 2005
 */
package org.cobra_grendel.html.parser;

import java.io.InputStream;

import org.xml.sax.InputSource;

/**
 * The <code>InputSourceImpl</code> class implements the
 * <code>InputSource</code> interface.
 * 
 * @author J. H. S.
 */
public class InputSourceImpl extends InputSource
{
	/**
	 * Constructs an <code>InputSourceImpl</code>.
	 */
	public InputSourceImpl()
	{
		super();
	}
	

// TODO UCdetector: Remove unused code: 
// 	/**
// 	 * Constructs an <code>InputSourceImpl</code>.
// 	 * 
// 	 * @param byteStream
// 	 *            The input stream where content can be read.
// 	 * @deprecated Use constructor with <code>uri</code> parameter.
// 	 */
// 	@Deprecated
// 	public InputSourceImpl(InputStream byteStream)
// 	{
// 		super(byteStream);
// 	}
	
	/**
	 * Constructs an <code>InputSourceImpl</code>.
	 * 
	 * @param byteStream
	 *            The input stream where content can be read.
	 * @param uri
	 *            The URI that identifies the content.
	 * @param charset
	 *            The character set of the input stream.
	 */
	public InputSourceImpl(InputStream byteStream, String uri, String charset)
	{
		super(byteStream);
		setEncoding(charset);
		setSystemId(uri);
		setPublicId(uri);
	}
	

// TODO UCdetector: Remove unused code: 
// 	/**
// 	 * Constructs an <code>InputSourceImpl</code>.
// 	 * 
// 	 * @param characterStream
// 	 *            The <code>Reader</code> where characters can be read.
// 	 * @deprecated Use constructor with <code>uri</code> parameter.
// 	 */
// 	@Deprecated
// 	public InputSourceImpl(Reader characterStream)
// 	{
// 		super(characterStream);
// 	}
	

// TODO UCdetector: Remove unused code: 
// 	/**
// 	 * Constructs an <code>InputSourceImpl</code>.
// 	 * 
// 	 * @param characterStream
// 	 *            The <code>Reader</code> where characters can be read.
// 	 * @param uri
// 	 *            The URI of the document.
// 	 */
// 	public InputSourceImpl(Reader characterStream, String uri)
// 	{
// 		super(characterStream);
// 		setSystemId(uri);
// 	}
	

// TODO UCdetector: Remove unused code: 
// 	/**
// 	 * Constructs an <code>InputSourceImpl</code>. Note that the parameter
// 	 * does not represent a string to be parsed. To parse a string, use a
// 	 * StringReader.
// 	 * 
// 	 * @param systemId
// 	 *            The system ID of the input source.
// 	 */
// 	public InputSourceImpl(String systemId)
// 	{
// 		super(systemId);
// 	}
}
