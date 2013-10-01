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
 * Created on Dec 3, 2005
 */
package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;

public class ChildHTMLCollection extends AbstractScriptableDelegate implements HTMLCollection
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final NodeImpl rootNode;
	
	/**
	 * @param node
	 */
	public ChildHTMLCollection(NodeImpl node, int transactionId)
	{
		super(transactionId);
		rootNode = node;
	}
	
	@Override public int getLength()
	{
		return rootNode.getChildCount();
	}
	
	public int indexOf(Node node)
	{
		return rootNode.getChildIndex(node);
	}
	
	@Override public Node item(int index)
	{
		return rootNode.getChildAtIndex(index);
	}
	
	@Override public Node namedItem(String name)
	{
		org.w3c.dom.Document doc = rootNode.getOwnerDocument();
		if (doc == null)
		{
			return null;
		}
		// TODO: This might get elements that are not descendents.
		Node node = doc.getElementById(name);
		if ((node != null) && (node.getParentNode() == rootNode))
		{
			return node;
		}
		return null;
	}
}
