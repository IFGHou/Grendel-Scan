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

import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLTableRowElement;

public class HTMLTableRowElementImpl extends HTMLElementImpl implements HTMLTableRowElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLTableRowElementImpl(final int transactionId)
    {
        super("TR", true, transactionId);
    }

    public HTMLTableRowElementImpl(final String name, final int transactionId)
    {
        super(name, true, transactionId);
    }

    @Override
    public void deleteCell(final int index) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl != null)
            {
                int size = nl.size();
                int trcount = 0;
                for (int i = 0; i < size; i++)
                {
                    Node node = (Node) nl.get(i);
                    if (node instanceof org.w3c.dom.html2.HTMLTableCellElement)
                    {
                        if (trcount == index)
                        {
                            removeChildAt(index);
                        }
                        trcount++;
                    }
                }
            }
        }
        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range");
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public String getBgColor()
    {
        return getAttribute("bgcolor");
    }

    @Override
    public HTMLCollection getCells()
    {
        NodeFilter filter = new NodeFilter()
        {
            @Override
            public boolean accept(final Node node)
            {
                return node instanceof HTMLTableCellElementImpl;
            }
        };
        return new DescendentHTMLCollection(this, filter, transactionId);
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
    public int getRowIndex()
    {
        NodeImpl parent = (NodeImpl) getParentNode();
        if (parent == null)
        {
            return -1;
        }
        try
        {
            parent.visit(new NodeVisitor()
            {
                private int count = 0;

                @Override
                public void visit(final Node node)
                {
                    if (node instanceof HTMLTableRowElementImpl)
                    {
                        if (HTMLTableRowElementImpl.this == node)
                        {
                            throw new StopVisitorException(new Integer(count));
                        }
                        count++;
                    }
                }
            });
        }
        catch (StopVisitorException sve)
        {
            return ((Integer) sve.getTag()).intValue();
        }
        return -1;
    }

    @Override
    public int getSectionRowIndex()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getVAlign()
    {
        return getAttribute("valign");
    }

    @Override
    public HTMLElement insertCell(final int index) throws DOMException
    {
        org.w3c.dom.Document doc = document;
        if (doc == null)
        {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Orphan element");
        }
        HTMLElement cellElement = (HTMLElement) doc.createElement("TD");
        synchronized (treeLock)
        {
            if (index == -1)
            {
                appendChild(cellElement);
                return cellElement;
            }
            ArrayList nl = nodeList;
            if (nl != null)
            {
                int size = nl.size();
                int trcount = 0;
                for (int i = 0; i < size; i++)
                {
                    Node node = (Node) nl.get(i);
                    if (node instanceof org.w3c.dom.html2.HTMLTableCellElement)
                    {
                        if (trcount == index)
                        {
                            insertAt(cellElement, i);
                            return cellElement;
                        }
                        trcount++;
                    }
                }
            }
            else
            {
                appendChild(cellElement);
                return cellElement;
            }
        }
        throw new DOMException(DOMException.INDEX_SIZE_ERR, "Index out of range");
    }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
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
    public void setVAlign(final String vAlign)
    {
        setAttribute("valign", vAlign);
    }
}
