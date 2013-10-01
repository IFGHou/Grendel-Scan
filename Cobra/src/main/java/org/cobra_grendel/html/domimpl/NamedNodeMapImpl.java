/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Sep 3, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl extends AbstractScriptableDelegate implements NamedNodeMap
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final ArrayList attributeList = new ArrayList();
	// Note: class must be public for reflection to work.
	private final Map attributes = new HashMap();
	
	public NamedNodeMapImpl(Element owner, Map attribs, int transactionId)
	{
		super(transactionId);
		Iterator i = attribs.entrySet().iterator();
		while (i.hasNext())
		{
			Map.Entry entry = (Map.Entry) i.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			// TODO: "specified" attributes
			Attr attr = new AttrImpl(name, value, true, owner, "ID".equals(name), transactionId);
			attributes.put(name, attr);
			attributeList.add(attr);
		}
	}
	
	@Override public int getLength()
	{
		return attributeList.size();
	}
	
	@Override public Node getNamedItem(String name)
	{
		return (Node) attributes.get(name);
	}
	
	@Override public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}
	
	@Override public Node item(int index)
	{
		try
		{
			return (Node) attributeList.get(index);
		}
		catch (IndexOutOfBoundsException iob)
		{
			return null;
		}
	}
	
	/**
	 * @param name
	 */
	public Node namedItem(String name)
	{
		// Method needed for Javascript indexing.
		return getNamedItem(name);
	}
	
	@Override public Node removeNamedItem(String name) throws DOMException
	{
		return (Node) attributes.remove(name);
	}
	
	@Override public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}
	
	@Override public Node setNamedItem(Node arg) throws DOMException
	{
		Object prevValue = attributes.put(arg.getNodeName(), arg);
		if (prevValue != null)
		{
			attributeList.remove(prevValue);
		}
		attributeList.add(arg);
		return arg;
	}
	
	@Override public Node setNamedItemNS(Node arg) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "No namespace support");
	}
}
