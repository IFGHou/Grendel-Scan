package org.cobra_grendel.html.js;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Document;

public class Location extends AbstractScriptableDelegate
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(Location.class.getName());
    private String target;

    private final Window window;

    public Location(final Window window, final int transactionId)
    {
        super(transactionId);
        this.window = window;
    }

    public String getHash()
    {
        URL url = getURL();
        return url == null ? null : url.getRef();
    }

    public String getHost()
    {
        URL url = getURL();
        if (url == null)
        {
            return null;
        }
        return url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
    }

    public String getHostname()
    {
        URL url = getURL();
        if (url == null)
        {
            return null;
        }
        return url.getHost();
    }

    public String getHref()
    {
        Document document = window.getDocument();
        return document == null ? null : document.getDocumentURI();
    }

    public String getPathname()
    {
        URL url = getURL();
        return url == null ? null : url.getPath();
    }

    public String getPort()
    {
        URL url = getURL();
        if (url == null)
        {
            return null;
        }
        int port = url.getPort();
        return port == -1 ? null : String.valueOf(port);
    }

    public String getProtocol()
    {
        URL url = getURL();
        if (url == null)
        {
            return null;
        }
        return url.getProtocol() + ":";
    }

    public String getSearch()
    {
        URL url = getURL();
        String query = url == null ? null : url.getQuery();
        // Javascript requires "?" in its search string.
        return query == null ? "" : "?" + query;
    }

    public String getTarget()
    {
        return target;
    }

    private URL getURL()
    {
        URL url;
        try
        {
            Document document = window.getDocument();
            url = document == null ? null : new URL(document.getDocumentURI());
        }
        catch (java.net.MalformedURLException mfu)
        {
            url = null;
        }
        return url;
    }

    public void reload()
    {
        // TODO: This is not really reload.
        Document document = window.getDocument();
        if (document instanceof HTMLDocumentImpl)
        {
            HTMLDocumentImpl docImpl = (HTMLDocumentImpl) document;
            HtmlRendererContext rcontext = docImpl.getHtmlRendererContext();
            if (rcontext != null)
            {
                rcontext.reload();
            }
            else
            {
                docImpl.warn("reload(): No renderer context in Location's document.");
            }
        }
    }

    public void replace(final String href)
    {
        setHref(href);
    }

    public void setHref(final String uri)
    {
        HtmlRendererContext rcontext = window.getHtmlRendererContext();
        if (rcontext != null)
        {
            try
            {
                URL url;
                Document document = window.getDocument();
                if (document instanceof HTMLDocumentImpl)
                {
                    HTMLDocumentImpl docImpl = (HTMLDocumentImpl) document;
                    url = docImpl.getFullURL(uri);
                }
                else
                {
                    url = new URL(uri);
                }
                rcontext.navigate(url, target, transactionId);
            }
            catch (java.net.MalformedURLException mfu)
            {
                logger.log(Level.WARNING, "setHref(): Malformed location: [" + uri + "].", mfu);
            }
        }
    }

    public void setTarget(final String value)
    {
        target = value;
    }

    @Override
    public String toString()
    {
        // This needs to be href. Callers
        // rely on that.
        return getHref();
    }
}
