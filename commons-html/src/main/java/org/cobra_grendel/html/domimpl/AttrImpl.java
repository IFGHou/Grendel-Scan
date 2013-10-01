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
 * Created on Sep 10, 2005
 */
package org.cobra_grendel.html.domimpl;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public class AttrImpl extends NodeImpl implements Attr
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private boolean isId;
    private final String name;
    private final Element ownerElement;
    private final boolean specified;
    private String value;

    /**
     * @param name
     */
    public AttrImpl(final String name, final int transactionId)
    {
        super(transactionId);
        this.name = name;
        value = "";
        specified = false;
        ownerElement = null;
        isId = false;
    }

    /**
     * @param name
     * @param value
     */
    public AttrImpl(final String name, final String value, final boolean specified, final Element owner, final boolean isId, final int transactionId)
    {
        super(transactionId);
        this.name = name;
        this.value = value;
        this.specified = specified;
        ownerElement = owner;
        this.isId = isId;
    }

    @Override
    protected Node createSimilarNode()
    {
        return new AttrImpl(name, value, specified, ownerElement, isId, transactionId);
    }

    @Override
    public String getLocalName()
    {
        return name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getNodeName()
    {
        return name;
    }

    @Override
    public short getNodeType()
    {
        return Node.ATTRIBUTE_NODE;
    }

    @Override
    public String getNodeValue() throws DOMException
    {
        return value;
    }

    @Override
    public Element getOwnerElement()
    {
        return ownerElement;
    }

    @Override
    public TypeInfo getSchemaTypeInfo()
    {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
    }

    @Override
    public boolean getSpecified()
    {
        return specified;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public boolean isId()
    {
        return isId;
    }

    public void setId(final boolean value)
    {
        isId = value;
    }

    @Override
    public void setNodeValue(final String nodeValue) throws DOMException
    {
        value = nodeValue;
    }

    @Override
    public void setValue(final String value) throws DOMException
    {
        this.value = value;
    }
}
