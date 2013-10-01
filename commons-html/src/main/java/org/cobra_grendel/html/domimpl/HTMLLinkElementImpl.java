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
package org.cobra_grendel.html.domimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.util.gui.ColorFactory;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html2.HTMLBodyElement;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLLinkElement;

public class HTMLLinkElementImpl extends HTMLAbstractUIElement implements HTMLLinkElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HTMLLinkElementImpl.class.getName());
    private static final boolean loggableInfo = logger.isLoggable(Level.INFO);

    private boolean disabled;

    public HTMLLinkElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public boolean getDisabled()
    {
        return disabled;
    }

    @Override
    public String getHref()
    {
        return getAttribute("href");
    }

    @Override
    public String getHreflang()
    {
        return getAttribute("hreflang");
    }

    private java.awt.Color getLinkColor()
    {
        HTMLDocument doc = (HTMLDocument) document;
        if (doc != null)
        {
            HTMLBodyElement body = (HTMLBodyElement) doc.getBody();
            if (body != null)
            {
                String vlink = body.getVLink();
                String link = body.getLink();
                if (vlink != null || link != null)
                {
                    HtmlRendererContext rcontext = getHtmlRendererContext();
                    if (rcontext != null)
                    {
                        boolean visited = rcontext.isVisitedLink(this);
                        String colorText = visited ? vlink : link;
                        if (colorText != null)
                        {
                            return ColorFactory.getInstance().getColor(colorText);
                        }
                    }
                }
            }
        }
        return java.awt.Color.BLUE;
    }

    @Override
    public String getMedia()
    {
        return getAttribute("media");
    }

    @Override
    public String getRel()
    {
        return getAttribute("rel");
    }

    @Override
    public String getRev()
    {
        return getAttribute("rev");
    }

    @Override
    public String getTarget()
    {
        String target = getAttribute("target");
        if (target != null)
        {
            return target;
        }
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        return doc == null ? null : doc.getDefaultTarget();
    }

    @Override
    public String getType()
    {
        return getAttribute("type");
    }

    public void navigate()
    {
        if (disabled)
        {
            return;
        }
        HtmlRendererContext rcontext = getHtmlRendererContext();
        if (rcontext != null)
        {
            String href = getHref();
            if (href != null && !"".equals(href))
            {
                String target = getTarget();
                try
                {
                    URL url = getFullURL(href);
                    if (url == null)
                    {
                        this.warn("Unable to resolve URI: [" + href + "].");
                    }
                    else
                    {
                        rcontext.navigate(url, target, transactionId);
                    }
                }
                catch (MalformedURLException mfu)
                {
                    this.warn("Malformed URI: [" + href + "].", mfu);
                }
            }
        }
    }

    protected void processLink()
    {
        String rel = getAttribute("rel");
        // if(rel != null && rel.toLowerCase().indexOf("stylesheet") != -1)
        // {
        // String media = this.getMedia();
        // if(CSSUtilities.matchesMedia(media, this.getHtmlRendererContext())) {
        // HTMLDocumentImpl doc = (HTMLDocumentImpl) this.getOwnerDocument();
        // try {
        // boolean liflag = loggableInfo;
        // long time1 = liflag ? System.currentTimeMillis() : 0;
        // try {
        // CSSStyleSheet sheet = CSSUtilities.parse(this.getHref(), doc,
        // doc.getBaseURI(), false);
        // if(sheet != null) {
        // doc.addStyleSheet(sheet);
        // }
        // } finally {
        // if(liflag) {
        // long time2 = System.currentTimeMillis();
        // logger.info("processLink(): Loaded and parsed CSS (or attempted to)
        // at URI=[" + this.getHref() + "] in " + (time2 - time1) + " ms.");
        // }
        // }
        //
        // } catch(MalformedURLException mfe) {
        // this.warn("Will not parse CSS. URI=[" + this.getHref() + "] with
        // BaseURI=[" + doc.getBaseURI() + "] does not appear to be a valid
        // URI.");
        // } catch(Throwable err) {
        // this.warn("Unable to parse CSS. URI=[" + this.getHref() + "].", err);
        // }
        // }
        // }
    }

    @Override
    public void setDisabled(final boolean disabled)
    {
        this.disabled = disabled;
    }

    @Override
    public void setHref(final String href)
    {
        setAttribute("href", href);
    }

    @Override
    public void setHreflang(final String hreflang)
    {
        setAttribute("hreflang", hreflang);
    }

    @Override
    public void setMedia(final String media)
    {
        setAttribute("media", media);
    }

    @Override
    public void setRel(final String rel)
    {
        setAttribute("rel", rel);
    }

    @Override
    public void setRev(final String rev)
    {
        setAttribute("rev", rev);
    }

    @Override
    public void setTarget(final String target)
    {
        setAttribute("target", target);
    }

    @Override
    public void setType(final String type)
    {
        setAttribute("type", type);
    }

    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler)
    {
        if (org.cobra_grendel.html.parser.HtmlParser.MODIFYING_KEY.equals(key) && data != Boolean.TRUE)
        {
            processLink();
        }
        return super.setUserData(key, data, handler);
    }

    // protected RenderState createRenderState(RenderState prevRenderState) {
    // prevRenderState = new TextDecorationRenderState(prevRenderState,
    // RenderState.MASK_TEXTDECORATION_UNDERLINE);
    // prevRenderState = new ColorRenderState(prevRenderState,
    // this.getLinkColor());
    // return super.createRenderState(prevRenderState);
    // }
}
