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
 * Created on Oct 8, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.util.Map;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;

public class FilteredHTMLCollectionImpl extends AbstractScriptableDelegate implements HTMLCollection
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private class CounterNodeVisitor implements NodeVisitor
	{
		private int counter = 0;
		
		public int getCount()
		{
			return counter;
		}
		
		@Override public void visit(Node node)
		{
			if (filter.accept(node))
			{
				counter++;
			}
		}
	}
	
	private final NodeFilter filter;
	private final Object lock;
	private final NodeImpl rootNode;
	
	// Note: Class must be public for reflection to work.
	// OPTIMIZE: Indexers are inefficient in this class.
	private final Map sourceMap;
	
	public FilteredHTMLCollectionImpl(NodeImpl rootNode, Map sourceMap, NodeFilter filter, Object lock, int transactionId)
	{
		super(transactionId);
		this.sourceMap = sourceMap;
		this.filter = filter;
		this.lock = lock;
		this.rootNode = rootNode;
	}
	
	@Override public int getLength()
	{
		CounterNodeVisitor visitor = new CounterNodeVisitor();
		synchronized (lock)
		{
			rootNode.visitImpl(visitor);
			return visitor.getCount();
		}
	}
	
	@Override public Node item(final int index)
	{
		NodeVisitor visitor = new NodeVisitor()
		{
			private int counter = 0;
			
			@Override public void visit(Node node)
			{
				if (filter.accept(node))
				{
					if (counter == index)
					{
						throw new StopVisitorException(node);
					}
					counter++;
				}
			}
		};
		synchronized (lock)
		{
			try
			{
				rootNode.visitImpl(visitor);
				return null;
			}
			catch (StopVisitorException sve)
			{
				return (Node) sve.getTag();
			}
		}
	}
	
	@Override public Node namedItem(String name)
	{
		synchronized (lock)
		{
			Node node = (Node) sourceMap.get(name);
			if ((node != null) && filter.accept(node))
			{
				return node;
			}
			return null;
		}
	}
}
