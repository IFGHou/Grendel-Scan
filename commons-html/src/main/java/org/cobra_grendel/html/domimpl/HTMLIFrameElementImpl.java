package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.BrowserFrame;
import org.cobra_grendel.html.js.Window;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLIFrameElement;

public class HTMLIFrameElementImpl extends HTMLAbstractUIElement implements HTMLIFrameElement, FrameNode
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private volatile BrowserFrame browserFrame;

    public HTMLIFrameElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    protected void assignAttributeField(final String normalName, final String value)
    {
        if ("src".equals(normalName))
        {
            BrowserFrame frame = browserFrame;
            if (frame != null)
            {
                try
                {
                    frame.loadURL(getFullURL(value));
                }
                catch (java.net.MalformedURLException mfu)
                {
                    this.warn("assignAttributeField(): Unable to navigate to src.", mfu);
                }
            }
        }
        else
        {
            super.assignAttributeField(normalName, value);
        }
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public BrowserFrame getBrowserFrame()
    {
        return browserFrame;
    }

    @Override
    public Document getContentDocument()
    {
        // TODO: Domain-based security
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
        return getAttribute("frameborder");
    }

    @Override
    public String getHeight()
    {
        return getAttribute("height");
    }

    @Override
    public String getLongDesc()
    {
        return getAttribute("longdesc");
    }

    @Override
    public String getMarginHeight()
    {
        return getAttribute("marginheight");
    }

    @Override
    public String getMarginWidth()
    {
        return getAttribute("marginwidth");
    }

    @Override
    public String getName()
    {
        return getAttribute("name");
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
    public String getWidth()
    {
        return getAttribute("width");
    }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    @Override
    public void setBrowserFrame(final BrowserFrame frame)
    {
        browserFrame = frame;
        if (frame != null)
        {
            String src = getAttribute("src");
            if (src != null)
            {
                try
                {
                    frame.loadURL(getFullURL(src));
                }
                catch (java.net.MalformedURLException mfu)
                {
                    this.warn("setBrowserFrame(): Unable to navigate to src.", mfu);
                }
            }
        }
    }

    @Override
    public void setFrameBorder(final String frameBorder)
    {
        setAttribute("frameborder", frameBorder);
    }

    @Override
    public void setHeight(final String height)
    {
        setAttribute("height", height);
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
    public void setScrolling(final String scrolling)
    {
        setAttribute("scrolling", scrolling);
    }

    @Override
    public void setSrc(final String src)
    {
        setAttribute("src", src);
    }

    @Override
    public void setWidth(final String width)
    {
        setAttribute("width", width);
    }
}
