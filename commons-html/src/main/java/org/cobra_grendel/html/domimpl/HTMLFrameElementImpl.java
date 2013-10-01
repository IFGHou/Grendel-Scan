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
 * Created on Jan 28, 2006
 */
package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.BrowserFrame;
import org.cobra_grendel.html.js.Window;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLFrameElement;

public class HTMLFrameElementImpl extends HTMLElementImpl implements HTMLFrameElement, FrameNode
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private volatile BrowserFrame browserFrame;

    private boolean noResize;

    public HTMLFrameElementImpl(final String name, final boolean noStyleSheet, final int transactionId)
    {
        super(name, noStyleSheet, transactionId);
    }

    public HTMLFrameElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public BrowserFrame getBrowserFrame()
    {
        return browserFrame;
    }

    @Override
    public Document getContentDocument()
    {
        BrowserFrame frame = browserFrame;
        if (frame == null)
        {
            // Not loaded yet
            return null;
        }
        return frame.getContentDocument();
    }

    public Window getContentWindow()
    {
        BrowserFrame frame = browserFrame;
        if (frame == null)
        {
            // Not loaded yet
            return null;
        }
        return Window.getWindow(frame.getHtmlRendererContext());
    }

    @Override
    public String getFrameBorder()
    {
        return getAttribute("frameBorder");
    }

    @Override
    public String getLongDesc()
    {
        return getAttribute("longdesc");
    }

    @Override
    public String getMarginHeight()
    {
        return getAttribute("marginHeight");
    }

    @Override
    public String getMarginWidth()
    {
        return getAttribute("marginWidth");
    }

    @Override
    public String getName()
    {
        return getAttribute("name");
    }

    @Override
    public boolean getNoResize()
    {
        return noResize;
    }

    @Override
    public String getScrolling()
    {
        return getAttribute("scrolling");
    }

    @Override
    public String getSrc()
    {
        return getAttribute("src");
    }

    @Override
    public void setBrowserFrame(final BrowserFrame frame)
    {
        browserFrame = frame;
    }

    @Override
    public void setFrameBorder(final String frameBorder)
    {
        setAttribute("frameBorder", frameBorder);
    }

    @Override
    public void setLongDesc(final String longDesc)
    {
        setAttribute("longdesc", longDesc);
    }

    @Override
    public void setMarginHeight(final String marginHeight)
    {
        setAttribute("marginHeight", marginHeight);
    }

    @Override
    public void setMarginWidth(final String marginWidth)
    {
        setAttribute("marginWidth", marginWidth);
    }

    @Override
    public void setName(final String name)
    {
        setAttribute("name", name);
    }

    @Override
    public void setNoResize(final boolean noResize)
    {
        this.noResize = noResize;
    }

    @Override
    public void setScrolling(final String scrolling)
    {
        setAttribute("scrolling", scrolling);
    }

    @Override
    public void setSrc(final String src)
    {
        setAttribute("src", src);
    }

}
