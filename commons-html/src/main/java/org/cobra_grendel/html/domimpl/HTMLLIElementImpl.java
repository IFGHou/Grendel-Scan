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
 * Created on Feb 12, 2006
 */
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.w3c.dom.html2.HTMLLIElement;

public class HTMLLIElementImpl extends HTMLAbstractUIElement implements HTMLLIElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLLIElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getType()
    {
        return getAttribute("type");
    }

    @Override
    public int getValue()
    {
        String valueText = getAttribute("value");
        if (valueText == null)
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(valueText);
        }
        catch (NumberFormatException nfe)
        {
            return 0;
        }
    }

    @Override
    public void setType(final String type)
    {
        setAttribute("type", type);
    }

    @Override
    public void setValue(final int value)
    {
        setAttribute("value", String.valueOf(value));
    }

    // protected RenderState createRenderState(RenderState prevRenderState) {
    // return new DisplayRenderState(prevRenderState, this,
    // RenderState.DISPLAY_LIST_ITEM);
    // }
}
