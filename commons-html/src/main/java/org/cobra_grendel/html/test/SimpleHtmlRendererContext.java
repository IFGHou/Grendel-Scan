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
 * Created on Oct 22, 2005
 */
package org.cobra_grendel.html.test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import org.cobra_grendel.html.BrowserFrame;
import org.cobra_grendel.html.FormInput;
import org.cobra_grendel.html.HtmlObject;
import org.cobra_grendel.html.HtmlParserContext;
import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.domimpl.FrameNode;
import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.html.parser.DocumentBuilderImpl;
import org.cobra_grendel.html.parser.InputSourceImpl;
import org.cobra_grendel.util.io.RecordedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLLinkElement;

/**
 * The <code>SimpleHtmlRendererContext</code> class implements the {@link org.cobra_grendel.html.HtmlRendererContext} interface. Note that this class provides simple implementations of most methods,
 * which should be overridden to provide real-world functionality.
 */
public class SimpleHtmlRendererContext implements HtmlRendererContext
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHtmlRendererContext.class);
    private static final Set mediaNames = new HashSet();

    private UserAgentContext bcontext = null;

    private volatile HtmlRendererContext opener;

    private final HtmlRendererContext parentRcontext;

    private volatile String sourceCode;

    static
    {
        // Media names claimed by this context.
        Set mn = mediaNames;
        mn.add("screen");
        mn.add("tv");
        mn.add("tty");
        mn.add("all");
    }

    /**
     * Constructs a SimpleHtmlRendererContext.
     * 
     * @param contextComponent
     *            The component that will render HTML.
     */
    public SimpleHtmlRendererContext()
    {
        super();
        parentRcontext = null;
    }

    /**
     * Constructs a simple <code>HtmlRendererContext</code> without a parent. This constructor should not be used to create the context of a frame with a parent.
     * 
     * @param contextComponent
     * @param pcontext
     * @deprecated HtmlParserContext is no longer used in this class.
     */
    @Deprecated
    public SimpleHtmlRendererContext(final HtmlParserContext pcontext)
    {
        this(pcontext, null);
    }

    /**
     * Constructs a SimpleHtmlRendererContext.
     * 
     * @param contextComponent
     *            The component that will render HTML.
     * @param pcontext
     *            A parser context.
     * @param parentRcontext
     *            The parent's renderer context. This is <code>null</code> for the root renderer context. Normally ony frame renderer contexts would have parents.
     * @deprecated HtmlParserContext is no longer used in this class.
     */
    @Deprecated
    public SimpleHtmlRendererContext(final HtmlParserContext pcontext, final HtmlRendererContext parentRcontext)
    {
        super();
        this.parentRcontext = parentRcontext;
    }

    /**
     * Constructs a SimpleHtmlRendererContext.
     * 
     * @param contextComponent
     *            The component that will render HTML.
     * @param parentRcontext
     *            The parent's renderer context. This is <code>null</code> for the root renderer context. Normally ony frame renderer contexts would have parents.
     */
    public SimpleHtmlRendererContext(final HtmlRendererContext parentRcontext)
    {
        super();
        this.parentRcontext = parentRcontext;
    }

    @Override
    public void alert(final String message)
    {
    }

    @Override
    public void back()
    {
    }

    @Override
    public void blur()
    {
        this.warn("back(): Not overridden");
    }

    @Override
    public void close()
    {
        this.warn("close(): Not overridden");
    }

    // Methods useful to Window below:

    @Override
    public boolean confirm(final String message)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlContext#createBrowserFrame()
     */
    @Override
    public BrowserFrame createBrowserFrame()
    {
        return null;
    }

    /**
     * This method is called by the local navigate() implementation and creates a {@link SimpleHtmlParserContext}. Override this method if you need to use the local navigate() implementation with an
     * overridden parser context.
     */
    protected HtmlParserContext createParserContext(final java.net.URL url)
    {
        return new SimpleHtmlParserContext();
    }

    public void error(final String message)
    {
        LOGGER.error(message);
    }

    public void error(final String message, final Throwable throwable)
    {
        LOGGER.error(message, throwable);
    }

    @Override
    public void focus()
    {
        this.warn("focus(): Not overridden");
    }

    @Override
    public String getDefaultStatus()
    {
        this.warn("getDefaultStatus(): Not overridden");
        return "";
    }

    /**
     * This needs to be fixed for the scanner
     */
    @Override
    public HTMLCollection getFrames()
    {
        /*
         * Object rootNode = this.htmlPanel.getRootNode();
         * 
         * if(rootNode instanceof HTMLDocumentImpl) { return ((HTMLDocumentImpl) rootNode).getFrames(); } else { return null; }
         */
        return null;
    }

    @Override
    public HtmlObject getHtmlObject(final HTMLElement element)
    {
        HtmlObject result;
        if ("OBJECT".equalsIgnoreCase(element.getTagName()))
        {
            result = null;
        }
        else
        {
            result = new SimpleHtmlObject(element);
        }
        this.warn("getHtmlObject(): Not overridden; returning " + result + " for " + element + ".");
        return result;
    }

    public int getLength()
    {
        this.warn("getLength(): Not overridden");
        return 0;
    }

    @Override
    public String getName()
    {
        this.warn("getName(): Not overridden");
        return "";
    }

    @Override
    public HtmlRendererContext getOpener()
    {
        return opener;
    }

    @Override
    public HtmlRendererContext getParent()
    {
        return parentRcontext;
    }

    public String getSourceCode()
    {
        return sourceCode;
    }

    @Override
    public String getStatus()
    {
        this.warn("getStatus(): Not overridden");
        return "";
    }

    @Override
    public HtmlRendererContext getTop()
    {
        HtmlRendererContext ancestor = parentRcontext;
        if (ancestor == null)
        {
            return this;
        }
        return ancestor.getTop();
    }

    @Override
    public UserAgentContext getUserAgentContext()
    {
        this.warn("getUserAgentContext(): Not overridden; returning simple one.");
        synchronized (this)
        {
            if (bcontext == null)
            {
                bcontext = new SimpleUserAgentContext();
            }
            return bcontext;
        }
    }

    @Override
    public boolean isClosed()
    {
        this.warn("isClosed(): Not overridden");
        return false;
    }

    @Override
    public boolean isMedia(final String mediaName)
    {
        return mediaNames.contains(mediaName.toLowerCase());
    }

    /**
     * Should be overridden to return true if the link has been visited.
     */
    @Override
    public boolean isVisitedLink(final HTMLLinkElement link)
    {
        // TODO
        return false;
    }

    /**
     * Implements simple navigation with incremental rendering, and target processing, including frame lookup. Should be overridden to allow for more robust browser navigation.
     * <p>
     * <b>Notes:</b>
     * <ul>
     * <li>Encoding ISO-8859-1 assumed always.
     * <li>Caching is not implemented.
     * <li>Cookies are not implemented.
     * <li>Incremental rendering is not optimized for ignorable document change notifications.
     * <li>Other HTTP features are not implemented.
     * </ul>
     */
    @Override
    public void navigate(final URL href, String target, final int referingTransactionId)
    {
        // This method implements simple incremental rendering.
        if (target != null)
        {
            HtmlRendererContext topCtx = getTop();
            HTMLCollection frames = topCtx.getFrames();
            if (frames != null)
            {
                org.w3c.dom.Node frame = frames.namedItem(target);
                if (frame instanceof FrameNode)
                {
                    BrowserFrame bframe = ((FrameNode) frame).getBrowserFrame();
                    if (bframe == null)
                    {
                        throw new IllegalStateException("Frame node without a BrowserFrame instance: " + frame);
                    }
                    if (bframe.getHtmlRendererContext() != this)
                    {
                        bframe.loadURL(href);
                        return;
                    }
                }
            }
            target = target.trim().toLowerCase();
            if ("_top".equals(target))
            {
                getTop().navigate(href, null, referingTransactionId);
                return;
            }
            else if ("_parent".equals(target))
            {
                HtmlRendererContext parent = getParent();
                if (parent != null)
                {
                    parent.navigate(href, null, referingTransactionId);
                    return;
                }
            }
            else if ("_blank".equals(target))
            {
                this.open(href.toExternalForm(), "cobra.blank", "", false);
                return;
            }
            else
            {
                // fall through
            }
        }

        URL urlForLoading;
        if (href.getProtocol().equals("file"))
        {
            // Remove query so it works.
            try
            {
                urlForLoading = new URL(href.getProtocol(), href.getHost(), href.getPort(), href.getPath());
            }
            catch (java.net.MalformedURLException throwable)
            {
                this.warn("malformed", throwable);
                urlForLoading = href;
            }
        }
        else
        {
            urlForLoading = href;
        }
        final URL finalURLForLoading = urlForLoading;
        // Make request asynchronously.
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    URL uri = href;
                    LOGGER.info("process(): Loading URI=[" + uri + "].");
                    long time0 = System.currentTimeMillis();
                    // Using potentially different URL for loading.
                    URLConnection connection = finalURLForLoading.openConnection();
                    connection.setRequestProperty("User-Agent", getUserAgentContext().getUserAgent());
                    connection.setRequestProperty("Cookie", "");
                    if (connection instanceof HttpURLConnection)
                    {
                        HttpURLConnection hc = (HttpURLConnection) connection;
                        hc.setInstanceFollowRedirects(true);
                        int responseCode = hc.getResponseCode();
                        LOGGER.info("process(): HTTP response code: " + responseCode);
                    }
                    InputStream in = connection.getInputStream();
                    try
                    {
                        sourceCode = null;
                        long time1 = System.currentTimeMillis();
                        RecordedInputStream rin = new RecordedInputStream(in);
                        InputStream bin = new BufferedInputStream(rin, 8192);
                        HtmlParserContext pcontext = createParserContext(uri);
                        DocumentBuilderImpl builder = new DocumentBuilderImpl(pcontext, SimpleHtmlRendererContext.this);
                        String actualURI = uri.toExternalForm();
                        // Only create document, don't parse.
                        HTMLDocumentImpl document = (HTMLDocumentImpl) builder.createDocument(new InputSourceImpl(bin, actualURI, "ISO-8859-1"), referingTransactionId);
                        // Set document in HtmlPanel. Safe to call outside GUI
                        // thread.
                        // SimpleHtmlRendererContext.this.htmlPanel.setDocument(document,
                        // SimpleHtmlRendererContext.this);
                        // Now start loading.
                        document.load();
                        long time2 = System.currentTimeMillis();
                        LOGGER.info("Parsed URI=[" + uri + "]: Parse elapsed: " + (time2 - time1) + " ms. Connection elapsed: " + (time1 - time0) + " ms.");
                        sourceCode = rin.getString("ISO-8859-1");
                    }
                    finally
                    {
                        in.close();
                    }
                }
                catch (Exception err)
                {
                    SimpleHtmlRendererContext.this.error("navigate(): Error loading or parsing request.", err);
                }
            }
        }.start();
    }

    @Override
    public HtmlRendererContext open(final java.net.URL url, final String windowName, final String windowFeatures, final boolean replace)
    {
        return null;
    }

    @Override
    public HtmlRendererContext open(final String url, final String windowName, final String windowFeatures, final boolean replace)
    {
        return null;
    }

    @Override
    public String prompt(final String message, final String inputDefault)
    {
        return "";
    }

    /**
     * Implements reload as navigation to current URL. Override to implement a more robust reloading mechanism.
     */
    @Override
    public void reload()
    {
    }

    @Override
    public void scroll(final int x, final int y)
    {
        this.warn("scroll(): Not overridden");
    }

    @Override
    public void setDefaultStatus(final String message)
    {
        this.warn("setDefaultStatus(): Not overridden.");
    }

    @Override
    public void setOpener(final HtmlRendererContext opener)
    {
        this.opener = opener;
    }

    @Override
    public void setStatus(final String message)
    {
        this.warn("setStatus(): Not overridden");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.HtmlContext#submitForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.xamjwg.html.FormInput[])
     */
    @Override
    public void submitForm(final String method, final java.net.URL action, final String target, final String enctype, final FormInput[] formInputs)
    {
        StringBuffer sb = new StringBuffer();
        String lineBreak = System.getProperty("line.separator");
        if (formInputs != null)
        {
            for (FormInput formInput : formInputs)
            {
                sb.append("INPUT: " + formInput.toString());
                sb.append(lineBreak);
            }
        }
        this.warn("submitForm(): Not overridden; method=" + method + "; action=" + action + "; target=" + target + "; enctype=" + enctype + lineBreak + sb);
    }

    public void warn(final String message)
    {
        LOGGER.warn(message);
    }

    public void warn(final String message, final Throwable throwable)
    {
        LOGGER.warn(message, throwable);
    }
}
