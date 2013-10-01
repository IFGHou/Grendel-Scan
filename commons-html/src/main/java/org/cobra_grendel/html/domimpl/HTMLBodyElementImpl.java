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
 * Created on Oct 8, 2005
 */
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.mozilla.javascript.Function;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLBodyElement;
import org.w3c.dom.html2.HTMLDocument;

public class HTMLBodyElementImpl extends HTMLAbstractUIElement implements HTMLBodyElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLBodyElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    protected void assignAttributeField(final String normalName, final String value)
    {
        if ("onload".equals(normalName))
        {
            Function onload = getEventFunction(null, normalName);
            if (onload != null)
            {
                setOnload(onload);
            }
        }
        else
        {
            super.assignAttributeField(normalName, value);
        }
    }

    @Override
    public String getALink()
    {
        return getAttribute("alink");
    }

    @Override
    public String getBackground()
    {
        return getAttribute("background");
    }

    @Override
    public String getBgColor()
    {
        return getAttribute("bgcolor");
    }

    @Override
    public String getLink()
    {
        return getAttribute("link");
    }

    /*
     * protected RenderState createRenderState(RenderState prevRenderState) { return new BodyRenderState(prevRenderState, this); }
     */
    public Function getOnload()
    {
        Object document = this.document;
        if (document instanceof HTMLDocumentImpl)
        {
            return ((HTMLDocumentImpl) document).getOnloadHandler();
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getText()
    {
        return getAttribute("text");
    }

    @Override
    public String getVLink()
    {
        return getAttribute("vlink");
    }

    @Override
    public void setALink(final String aLink)
    {
        setAttribute("alink", aLink);
    }

    @Override
    public void setBackground(final String background)
    {
        setAttribute("background", background);
    }

    @Override
    public void setBgColor(final String bgColor)
    {
        setAttribute("bgcolor", bgColor);
    }

    @Override
    public void setLink(final String link)
    {
        setAttribute("link", link);
    }

    public void setOnload(final Function onload)
    {
        Object document = this.document;
        if (document instanceof HTMLDocumentImpl)
        {
            // Note that body.onload overrides
            // Window.onload.
            ((HTMLDocumentImpl) document).setOnloadHandler(onload);
        }
    }

    @Override
    void setOwnerDocument(final Document value)
    {
        super.setOwnerDocument(value);
        if (value instanceof HTMLDocument)
        {
            ((HTMLDocument) value).setBody(this);
        }
    }

    @Override
    void setOwnerDocument(final Document value, final boolean deep)
    {
        super.setOwnerDocument(value, deep);
        if (value instanceof HTMLDocument)
        {
            ((HTMLDocument) value).setBody(this);
        }
    }

    @Override
    public void setText(final String text)
    {
        setAttribute("text", text);
    }

    @Override
    public void setVLink(final String vLink)
    {
        setAttribute("vlink", vLink);
    }

}
