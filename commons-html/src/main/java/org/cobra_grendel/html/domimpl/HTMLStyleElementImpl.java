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
 * Created on Nov 27, 2005
 */
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.CSSUtilities;
// import org.w3c.css.sac.InputSource;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html2.HTMLStyleElement;

public class HTMLStyleElementImpl extends HTMLElementImpl implements HTMLStyleElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private boolean disabled;

    public HTMLStyleElementImpl(final int transactionId)
    {
        super("STYLE", true, transactionId);
    }

    public HTMLStyleElementImpl(final String name, final int transactionId)
    {
        super(name, true, transactionId);
    }

    @Override
    public boolean getDisabled()
    {
        return disabled;
    }

    @Override
    public String getMedia()
    {
        return getAttribute("media");
    }

    @Override
    public String getType()
    {
        return getAttribute("type");
    }

    @Override
    public void setDisabled(final boolean disabled)
    {
        this.disabled = disabled;
    }

    @Override
    public void setMedia(final String media)
    {
        setAttribute("media", media);
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
            // this.processStyle();
        }
        return super.setUserData(key, data, handler);
    }

    /*
     * protected void processStyle() { if(CSSUtilities.matchesMedia(this.getMedia(), this.getHtmlRendererContext())) { String text = this.getRawInnerText(true); if(text != null && !"".equals(text)) {
     * String processedText = CSSUtilities.preProcessCss(text); HTMLDocumentImpl doc = (HTMLDocumentImpl) this.getOwnerDocument(); // CSSOMParser parser = new CSSOMParser(); InputSource is =
     * CSSUtilities.getCssInputSourceForStyleSheet(processedText); try { CSSStyleSheet sheet = parser.parseStyleSheet(is); doc.addStyleSheet(sheet); } catch(Throwable err) { this.warn("Unable to parse
     * style sheet", err); } } } }
     */
}
