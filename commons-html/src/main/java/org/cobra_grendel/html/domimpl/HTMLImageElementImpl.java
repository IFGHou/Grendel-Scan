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
 * Created on Nov 19, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.util.ArrayList;

import org.cobra_grendel.html.js.Executor;
import org.mozilla.javascript.Function;
import org.w3c.dom.html2.HTMLImageElement;

public class HTMLImageElementImpl extends HTMLAbstractUIElement implements HTMLImageElement
{
    private class LocalImageListener implements ImageListener
    {
        private final String expectedImgSrc;

        public LocalImageListener(final String imgSrc)
        {
            expectedImgSrc = imgSrc;
        }

        @Override
        public void imageLoaded(final ImageEvent event)
        {
            dispatchEvent(expectedImgSrc, event);
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private java.awt.Image image = null;

    private String imageSrc;

    private final ArrayList listeners = new ArrayList(1);

    private Function onload;

    public HTMLImageElementImpl(final int transactionId)
    {
        super("IMG", transactionId);
    }

    public HTMLImageElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    /**
     * Adds a listener of image loading events. The listener gets called right away if there's already an image.
     * 
     * @param listener
     */
    public void addImageListener(final ImageListener listener)
    {
        ArrayList l = listeners;
        java.awt.Image currentImage;
        synchronized (l)
        {
            currentImage = image;
            l.add(listener);
        }
        if (currentImage != null)
        {
            // Call listener right away if there's already an
            // image; holding no locks.
            listener.imageLoaded(new ImageEvent(this, currentImage));
            // Should not call onload handler here. That's taken
            // care of otherwise.
        }
    }

    @Override
    protected void assignAttributeField(final String normalName, final String value)
    {
        super.assignAttributeField(normalName, value);
        if ("src".equals(normalName))
        {
            loadImage(value);
        }
    }

    private void dispatchEvent(final String expectedImgSrc, final ImageEvent event)
    {
        ArrayList l = listeners;
        ImageListener[] listenerArray;
        synchronized (l)
        {
            if (!expectedImgSrc.equals(imageSrc))
            {
                return;
            }
            image = event.image;
            // Get array of listeners while holding lock.
            listenerArray = (ImageListener[]) l.toArray(ImageListener.EMPTY_ARRAY);
        }
        int llength = listenerArray.length;
        for (int i = 0; i < llength; i++)
        {
            // Inform listener, holding no lock.
            listenerArray[i].imageLoaded(event);
        }
        Function onload = getOnload();
        if (onload != null)
        {
            // TODO: onload event object?
            Executor.executeFunction(HTMLImageElementImpl.this, onload, null);
        }
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public String getAlt()
    {
        return getAttribute("alt");
    }

    @Override
    public String getBorder()
    {
        return getAttribute("border");
    }

    @Override
    public int getHeight()
    {
        UINode r = uiNode;
        return r == null ? 0 : r.getBounds().height;
    }

    @Override
    public int getHspace()
    {
        return getAttributeAsInt("hspace", 0);
    }

    public final java.awt.Image getImage()
    {
        synchronized (listeners)
        {
            return image;
        }
    }

    @Override
    public boolean getIsMap()
    {
        return getAttributeAsBoolean("isMap");
    }

    @Override
    public String getLongDesc()
    {
        return getAttribute("longDesc");
    }

    @Override
    public String getName()
    {
        return getAttribute("name");
    }

    public Function getOnload()
    {
        return getEventFunction(onload, "onload");
    }

    @Override
    public String getSrc()
    {
        return getAttribute("src");
    }

    @Override
    public String getUseMap()
    {
        return getAttribute("useMap");
    }

    @Override
    public int getVspace()
    {
        return getAttributeAsInt("vspace", 0);
    }

    @Override
    public int getWidth()
    {
        UINode r = uiNode;
        return r == null ? 0 : r.getBounds().width;
    }

    private void loadImage(final String src)
    {
        HTMLDocumentImpl document = (HTMLDocumentImpl) this.document;
        if (document != null)
        {
            synchronized (listeners)
            {
                imageSrc = src;
                image = null;
            }
            if (src != null)
            {
                document.loadImage(src, new LocalImageListener(src));
            }
        }
    }

    public void removeImageListener(final ImageListener listener)
    {
        ArrayList l = listeners;
        synchronized (l)
        {
            l.remove(l);
        }
    }

    // /* (non-Javadoc)
    // * @see org.xamjwg.html.renderer.RenderableContext#getHeightLength()
    // */
    // public HtmlLength getHeightLength() {
    // return this.heightLength;
    // }
    //
    // /* (non-Javadoc)
    // * @see org.xamjwg.html.renderer.RenderableContext#getWidthLength()
    // */
    // public HtmlLength getWidthLength() {
    // return this.widthLength;
    // }

    // /* (non-Javadoc)
    // * @see org.xamjwg.html.renderer.RenderableContext#getAlignmentX()
    // */
    // public float getAlignmentX() {
    // return 0.5f;
    // }
    //
    // /* (non-Javadoc)
    // * @see org.xamjwg.html.renderer.RenderableContext#getAlignmentY()
    // */
    // public float getAlignmentY() {
    // return this.alignmentY;
    // }

    // private HtmlLength widthLength;
    // private HtmlLength heightLength;
    // private float alignmentY = 1.0f;

    // /* (non-Javadoc)
    // * @see
    // org.xamjwg.html.domimpl.ElementImpl#assignAttributeField(java.lang.String,
    // java.lang.String)
    // */
    // protected void assignAttributeField(String normalName, String value) {
    // super.assignAttributeField(normalName, value);
    // if("width".equals(normalName)) {
    // try {
    // this.widthLength = new HtmlLength(value);
    // } catch(Exception err) {
    // this.warn("Bad width spec: " + value, err);
    // this.widthLength = null;
    // }
    // }
    // else if("height".equals(normalName)) {
    // try {
    // this.heightLength = new HtmlLength(value);
    // } catch(Exception err) {
    // this.warn("Bad height spec: " + value, err);
    // this.heightLength = null;
    // }
    // }
    // }
    //
    // private final void assignAlignment(String value) {
    // if(value.equalsIgnoreCase("middle")) {
    // this.alignmentY = 0.5f;
    // }
    // else if(value.equalsIgnoreCase("top")) {
    // this.alignmentY = 0.0f;
    // }
    // else if(value.equalsIgnoreCase("bottom")) {
    // this.alignmentY = 1.0f;
    // }
    // else {
    // this.alignmentY = 1.0f;
    // }
    // }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    @Override
    public void setAlt(final String alt)
    {
        setAttribute("alt", alt);
    }

    @Override
    public void setBorder(final String border)
    {
        setAttribute("border", border);
    }

    @Override
    public void setHeight(final int height)
    {
        setAttribute("height", String.valueOf(height));
    }

    @Override
    public void setHspace(final int hspace)
    {
        setAttribute("hspace", String.valueOf("hspace"));
    }

    @Override
    public void setIsMap(final boolean isMap)
    {
        setAttribute("isMap", isMap ? "isMap" : null);
    }

    @Override
    public void setLongDesc(final String longDesc)
    {
        setAttribute("longDesc", longDesc);
    }

    @Override
    public void setName(final String name)
    {
        setAttribute("name", name);
    }

    public void setOnload(final Function onload)
    {
        this.onload = onload;
    }

    /**
     * Sets the image URI and starts to load the image. Note that an HtmlRendererContext should be available to the HTML document for images to be loaded.
     */
    @Override
    public void setSrc(final String src)
    {
        setAttribute("src", src);
    }

    @Override
    public void setUseMap(final String useMap)
    {
        setAttribute("useMap", useMap);
    }

    @Override
    public void setVspace(final int vspace)
    {
        setAttribute("vspace", String.valueOf(vspace));
    }

    @Override
    public void setWidth(final int width)
    {
        setAttribute("width", String.valueOf(width));
    }
}
