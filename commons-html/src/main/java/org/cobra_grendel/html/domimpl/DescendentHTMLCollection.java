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
 * Created on Dec 3, 2005
 */
package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;

public class DescendentHTMLCollection extends AbstractScriptableDelegate implements HTMLCollection
{
    private final class NodeCounter implements NodeVisitor
    {
        private int count = 0;

        public int getCount()
        {
            return count;
        }

        @Override
        public final void visit(final Node node)
        {
            if (nodeFilter.accept(node))
            {
                count++;
                throw new SkipVisitorException();
            }
        }
    }

    private final class NodeScanner implements NodeVisitor
    {
        private int count = 0;
        private Node foundNode = null;
        private final int targetIndex;

        public NodeScanner(final int idx)
        {
            targetIndex = idx;
        }

        public Node getNode()
        {
            return foundNode;
        }

        @Override
        public final void visit(final Node node)
        {
            if (nodeFilter.accept(node))
            {
                if (count == targetIndex)
                {
                    foundNode = node;
                    throw new StopVisitorException();
                }
                count++;
                throw new SkipVisitorException();
            }
        }
    }

    private final class NodeScanner2 implements NodeVisitor
    {
        private int count = 0;
        private int foundIndex = -1;
        private final Node targetNode;

        public NodeScanner2(final Node node)
        {
            targetNode = node;
        }

        public int getIndex()
        {
            return foundIndex;
        }

        @Override
        public final void visit(final Node node)
        {
            if (nodeFilter.accept(node))
            {
                if (node == targetNode)
                {
                    foundIndex = count;
                    throw new StopVisitorException();
                }
                count++;
                throw new SkipVisitorException();
            }
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final NodeFilter nodeFilter;

    // TODO: This collection is very inefficient for iteration.
    private final NodeImpl rootNode;

    /**
     * @param node
     * @param filter
     */
    public DescendentHTMLCollection(final NodeImpl node, final NodeFilter filter, final int transactionId)
    {
        super(transactionId);
        rootNode = node;
        nodeFilter = filter;
    }

    @Override
    public int getLength()
    {
        NodeCounter nc = new NodeCounter();
        rootNode.visit(nc);
        return nc.getCount();
    }

    public int indexOf(final Node node)
    {
        NodeScanner2 ns = new NodeScanner2(node);
        try
        {
            rootNode.visit(ns);
        }
        catch (StopVisitorException sve)
        {
            // ignore
        }
        return ns.getIndex();
    }

    @Override
    public Node item(final int index)
    {
        NodeScanner ns = new NodeScanner(index);
        try
        {
            rootNode.visit(ns);
        }
        catch (StopVisitorException sve)
        {
            // ignore
        }
        return ns.getNode();
    }

    @Override
    public Node namedItem(final String name)
    {
        org.w3c.dom.Document doc = rootNode.getOwnerDocument();
        if (doc == null)
        {
            return null;
        }
        // TODO: This might get elements that are not descendents.
        Node node = doc.getElementById(name);
        if (node != null && nodeFilter.accept(node))
        {
            return node;
        }
        return null;
    }

}
