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
 * Created on Sep 3, 2005
 */
package org.cobra_grendel.html.domimpl;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract class CharacterDataImpl extends NodeImpl implements CharacterData
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected volatile String text;

    public CharacterDataImpl(final int transactionId)
    {
        super(transactionId);
    }

    public CharacterDataImpl(final String text, final int transactionId)
    {
        this(transactionId);
        this.text = text;
    }

    @Override
    public void appendData(final String arg) throws DOMException
    {
        text += arg;
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.domimpl.NodeImpl#cloneNode(boolean)
     */
    @Override
    public Node cloneNode(final boolean deep)
    {
        CharacterDataImpl newNode = (CharacterDataImpl) super.cloneNode(deep);
        newNode.setData(getData());
        return newNode;
    }

    @Override
    public void deleteData(final int offset, final int count) throws DOMException
    {
        StringBuffer buffer = new StringBuffer(text);
        StringBuffer result = buffer.delete(offset, offset + count);
        text = result.toString();
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    public String getClassName()
    {
        return "HTMLCharacterData";
    }

    @Override
    public String getData() throws DOMException
    {
        return text;
    }

    @Override
    public int getLength()
    {
        return text.length();
    }

    @Override
    public String getTextContent() throws DOMException
    {
        return text;
    }

    @Override
    public void insertData(final int offset, final String arg) throws DOMException
    {
        StringBuffer buffer = new StringBuffer(text);
        StringBuffer result = buffer.insert(offset, arg);
        text = result.toString();
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    @Override
    public void replaceData(final int offset, final int count, final String arg) throws DOMException
    {
        StringBuffer buffer = new StringBuffer(text);
        StringBuffer result = buffer.replace(offset, offset + count, arg);
        text = result.toString();
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    @Override
    public void setData(final String data) throws DOMException
    {
        text = data;
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    @Override
    public void setTextContent(final String textContent) throws DOMException
    {
        text = textContent;
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    @Override
    public String substringData(final int offset, final int count) throws DOMException
    {
        return text.substring(offset, offset + count);
    }

    @Override
    public String toString()
    {
        String someText = text;
        int length = someText.length();
        if (someText != null && someText.length() > 32)
        {
            someText = someText.substring(0, 29) + "...";
        }
        return getNodeName() + "[length=" + length + ",text=" + someText + "]";
    }

}
