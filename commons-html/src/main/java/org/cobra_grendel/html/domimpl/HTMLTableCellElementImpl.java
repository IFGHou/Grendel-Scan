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
 * Created on Dec 4, 2005
 */
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.w3c.dom.html2.HTMLTableCellElement;

public class HTMLTableCellElementImpl extends HTMLAbstractUIElement implements HTMLTableCellElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLTableCellElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getAbbr()
    {
        return getAttribute("abbr");
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public String getAxis()
    {
        return getAttribute("axis");
    }

    @Override
    public String getBgColor()
    {
        return getAttribute("bgcolor");
    }

    @Override
    public int getCellIndex()
    {
        // TODO Cell index in row
        return 0;
    }

    @Override
    public String getCh()
    {
        return getAttribute("ch");
    }

    @Override
    public String getChOff()
    {
        return getAttribute("choff");
    }

    @Override
    public int getColSpan()
    {
        String colSpanText = getAttribute("colspan");
        if (colSpanText == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt(colSpanText);
            }
            catch (NumberFormatException nfe)
            {
                return 1;
            }
        }
    }

    @Override
    public String getHeaders()
    {
        return getAttribute("headers");
    }

    @Override
    public String getHeight()
    {
        return getAttribute("height");
    }

    @Override
    public boolean getNoWrap()
    {
        return "nowrap".equalsIgnoreCase(getAttribute("nowrap"));
    }

    @Override
    public int getRowSpan()
    {
        String rowSpanText = getAttribute("rowspan");
        if (rowSpanText == null)
        {
            return 1;
        }
        else
        {
            try
            {
                return Integer.parseInt(rowSpanText);
            }
            catch (NumberFormatException nfe)
            {
                return 1;
            }
        }
    }

    @Override
    public String getScope()
    {
        return getAttribute("scope");
    }

    @Override
    public String getVAlign()
    {
        return getAttribute("valign");
    }

    @Override
    public String getWidth()
    {
        return getAttribute("width");
    }

    @Override
    public void setAbbr(final String abbr)
    {
        setAttribute("abbr", abbr);
    }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    @Override
    public void setAxis(final String axis)
    {
        setAttribute("axis", axis);
    }

    @Override
    public void setBgColor(final String bgColor)
    {
        setAttribute("bgcolor", bgColor);
    }

    @Override
    public void setCh(final String ch)
    {
        setAttribute("ch", ch);
    }

    @Override
    public void setChOff(final String chOff)
    {
        setAttribute("choff", chOff);
    }

    @Override
    public void setColSpan(final int colSpan)
    {
        setAttribute("colspan", String.valueOf(colSpan));
    }

    @Override
    public void setHeaders(final String headers)
    {
        setAttribute("headers", headers);
    }

    @Override
    public void setHeight(final String height)
    {
        setAttribute("height", height);
    }

    @Override
    public void setNoWrap(final boolean noWrap)
    {
        setAttribute("nowrap", noWrap ? "nowrap" : null);
    }

    @Override
    public void setRowSpan(final int rowSpan)
    {
        setAttribute("rowspan", String.valueOf(rowSpan));
    }

    @Override
    public void setScope(final String scope)
    {
        setAttribute("scope", scope);
    }

    @Override
    public void setVAlign(final String vAlign)
    {
        setAttribute("valign", vAlign);
    }

    @Override
    public void setWidth(final String width)
    {
        setAttribute("width", width);
    }

    // protected RenderState createRenderState(RenderState prevRenderState) {
    // return new TableCellRenderState(prevRenderState, this);
    // }
}
