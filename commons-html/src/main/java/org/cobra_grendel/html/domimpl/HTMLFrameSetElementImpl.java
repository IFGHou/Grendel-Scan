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

import org.w3c.dom.html2.HTMLFrameSetElement;

public class HTMLFrameSetElementImpl extends HTMLElementImpl implements HTMLFrameSetElement
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLFrameSetElementImpl(final String name, final boolean noStyleSheet, final int transactionId)
    {
        super(name, noStyleSheet, transactionId);
    }

    public HTMLFrameSetElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getCols()
    {
        return getAttribute("cols");
    }

    @Override
    public String getRows()
    {
        return getAttribute("rows");
    }

    @Override
    public void setCols(final String cols)
    {
        setAttribute("cols", cols);
    }

    @Override
    public void setRows(final String rows)
    {
        setAttribute("rows", rows);
    }
}
