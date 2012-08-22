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
import java.util.Collection;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl extends AbstractScriptableDelegate implements NodeList
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// Note: class must be public for reflection to work.
	private final ArrayList nodeList = new ArrayList();
	
	public NodeListImpl(Collection collection, int transactionId)
	{
		super(transactionId);
		nodeList.addAll(collection);
	}
	
	@Override public int getLength()
	{
		return nodeList.size();
	}
	
	@Override public Node item(int index)
	{
		try
		{
			return (Node) nodeList.get(index);
		}
		catch (IndexOutOfBoundsException iob)
		{
			return null;
		}
	}
}
