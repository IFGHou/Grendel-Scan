/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Oct 15, 2005
 */
package org.cobra_grendel.html.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang.NotImplementedException;
import org.cobra_grendel.html.HtmlParserContext;
import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.html.io.WritableLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <code>DocumentBuilderImpl</code> class is an HTML DOM parser that implements the standard W3C <code>DocumentBuilder</code> interface.
 * 
 * @author J. H. S.
 */
public class DocumentBuilderImpl extends DocumentBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBuilderImpl.class);
    private final UserAgentContext bcontext;
    private DOMImplementation domImplementation;
    private ErrorHandler errorHandler;
    private final HtmlRendererContext rcontext;

    private EntityResolver resolver;

    /**
     * @deprecated HtmlParserContext is no longer used.
     * @see #DocumentBuilderImpl(UserAgentContext)
     */
    @Deprecated
    public DocumentBuilderImpl(final HtmlParserContext context)
    {
        rcontext = null;
        bcontext = context.getUserAgentContext();
    }

    /**
     * @deprecated HtmlParserContext is no longer used.
     * @see #DocumentBuilderImpl(UserAgentContext,HtmlRendererContext)
     */
    @Deprecated
    public DocumentBuilderImpl(final HtmlParserContext context, final HtmlRendererContext rcontext)
    {
        this.rcontext = rcontext;
        bcontext = context.getUserAgentContext();
    }

    /**
     * Constructs a <code>DocumentBuilderImpl</code>. This constructor should be used when only the parsing functionality (without rendering) is required.
     * 
     * @param context
     *            An instance of {@link org.cobra_grendel.html.UserAgentContext}, which may be an instance of {@link org.cobra_grendel.html.test.SimpleUserAgentContext}.
     */
    public DocumentBuilderImpl(final UserAgentContext context)
    {
        rcontext = null;
        bcontext = context;
    }

    /**
     * Constructs a <code>DocumentBuilderImpl</code>. This constructor should be used when rendering is expected.
     * 
     * @param ucontext
     *            An instance of {@link org.cobra_grendel.html.UserAgentContext}, which may be an instance of {@link org.cobra_grendel.html.test.SimpleUserAgentContext}.
     * @param rcontext
     *            An instance of {@link org.cobra_grendel.html.HtmlRendererContext}, which may be an instance of {@link org.cobra_grendel.html.test.SimpleHtmlRendererContext}.
     */
    public DocumentBuilderImpl(final UserAgentContext ucontext, final HtmlRendererContext rcontext)
    {
        this.rcontext = rcontext;
        bcontext = ucontext;
    }

    /**
     * Creates a document without parsing it so it can be used for incremental rendering.
     * 
     * @param is
     *            The input source, which may be an instance of {@link org.cobra_grendel.html.parser.InputSourceImpl}.
     */
    public Document createDocument(final InputSource is, final int transactionId) throws SAXException, IOException
    {
        String charset = is.getEncoding();
        if (charset == null)
        {
            charset = "US-ASCII";
        }
        String uri = is.getSystemId();
        if (uri == null)
        {
            LOGGER.warn("parse(): InputSource has no SystemId (URI); document item URLs will not be resolvable.");
        }
        InputStream in = is.getByteStream();
        WritableLineReader wis;
        if (in != null)
        {
            wis = new WritableLineReader(new InputStreamReader(in, charset));
        }
        else
        {
            Reader reader = is.getCharacterStream();
            if (reader != null)
            {
                wis = new WritableLineReader(reader);
            }
            else
            {
                throw new IllegalArgumentException("InputSource has neither a byte stream nor a character stream");
            }
        }
        HTMLDocumentImpl document = new HTMLDocumentImpl(bcontext, rcontext, wis, uri, transactionId);
        return document;
    }

    // TODO: parseAsync

    @Override
    public DOMImplementation getDOMImplementation()
    {
        throw new NotImplementedException();
        // synchronized (this)
        // {
        // if (domImplementation == null)
        // {
        // domImplementation = new DOMImplementationImpl(bcontext);
        // }
        // return domImplementation;
        // }
    }

    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    public EntityResolver getResolver()
    {
        return resolver;
    }

    @Override
    public boolean isNamespaceAware()
    {
        return false;
    }

    @Override
    public boolean isValidating()
    {
        return false;
    }

    @Override
    public Document newDocument()
    {
        // return new HTMLDocumentImpl(bcontext);
        throw new NotImplementedException();
    }

    /**
     * Parser an HTML document given as an <code>InputSource</code>.
     * 
     * @param is
     *            The input source, which may be an instance of {@link org.cobra_grendel.html.parser.InputSourceImpl}.
     */
    @Override
    public Document parse(final InputSource is) throws org.xml.sax.SAXException, IOException
    {
        throw new NotImplementedException();
    }

    public Document parse(final InputSource is, final int transactionId) throws org.xml.sax.SAXException, IOException
    {
        HTMLDocumentImpl document = (HTMLDocumentImpl) createDocument(is, transactionId);
        document.load();
        return document;
    }

    @Override
    public void setEntityResolver(final EntityResolver er)
    {
        resolver = er;
    }

    @Override
    public void setErrorHandler(final ErrorHandler eh)
    {
        errorHandler = eh;
    }
}
