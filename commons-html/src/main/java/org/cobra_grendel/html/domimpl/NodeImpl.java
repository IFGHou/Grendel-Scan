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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.cobra_grendel.util.Objects;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public abstract class NodeImpl extends AbstractScriptableDelegate implements Node, ModelNode
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private static final NodeImpl[] EMPTY_ARRAY = new NodeImpl[0];
    // private static final RenderState INVALID_RENDER_STATE = new
    // StyleSheetRenderState(null, null);
    protected static final Logger logger = Logger.getLogger(NodeImpl.class.getName());
    private ChildHTMLCollection childrenCollection;
    private volatile String prefix;
    private Map userData;
    private String baseURI = "";

    // TODO: Inform handlers on cloning, etc.
    private List userDataHandlers;

    protected volatile Document document;

    protected ArrayList<NodeImpl> nodeList;

    protected volatile boolean notificationsSuspended = false;

    protected volatile Node parentNode;

    /**
     * A tree lock is less deadlock-prone than a node-level lock. This is assigned in setOwnerDocument.
     */
    protected volatile Object treeLock = this;

    protected UINode uiNode;

    public NodeImpl(final int transactionId)
    {
        super(transactionId);
    }

    // protected final Object getTreeLock() {
    // //TODO: Is this necessary? Why not use a lock per node?
    // Object doc = this.document;
    // return doc == null ? this : doc;
    // }
    //
    @Override
    public Node appendChild(final Node newChild) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl == null)
            {
                nl = new ArrayList(3);
                nodeList = nl;
            }
            nl.add(newChild);
            if (newChild instanceof NodeImpl)
            {
                ((NodeImpl) newChild).setParentImpl(this);
            }
        }

        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return newChild;
    }

    private void appendChildrenToCollectionImpl(final NodeFilter filter, final Collection collection)
    {
        ArrayList nl = nodeList;
        if (nl != null)
        {
            Iterator i = nl.iterator();
            while (i.hasNext())
            {
                NodeImpl node = (NodeImpl) i.next();
                if (filter.accept(node))
                {
                    collection.add(node);
                }
                node.appendChildrenToCollectionImpl(filter, collection);
            }
        }
    }

    /**
     * Sets the document node to null. This is so a single element can be referenced without retaining the entire DOM. If you start calling stuff that references parent nodes, ancestors, etc, expect a
     * null reference exception.
     * 
     * @param recursive
     */
    public void clearDocument(final boolean recursive)
    {
        if (document != null)
        {
            baseURI = document.getBaseURI();
        }
        setOwnerDocument(null, recursive);
    }

    /**
     * Sets the parent node to null.
     * 
     * @param recursive
     */
    public void clearParent()
    {
        setParentImpl(null);
    }

    @Override
    public Node cloneNode(final boolean deep)
    {
        try
        {
            Node newNode = createSimilarNode();
            NodeList children = getChildNodes();
            int length = children.getLength();
            for (int i = 0; i < length; i++)
            {
                Node child = children.item(i);
                Node newChild = deep ? child.cloneNode(deep) : child;
                newNode.appendChild(newChild);
            }
            if (newNode instanceof Element)
            {
                Element elem = (Element) newNode;
                NamedNodeMap nnmap = getAttributes();
                if (nnmap != null)
                {
                    int nnlength = nnmap.getLength();
                    for (int i = 0; i < nnlength; i++)
                    {
                        Attr attr = (Attr) nnmap.item(i);
                        elem.setAttributeNode((Attr) attr.cloneNode(true));
                    }
                }
            }
            return newNode;
        }
        catch (Exception err)
        {
            throw new IllegalStateException(err.getMessage());
        }
    }

    @Override
    public short compareDocumentPosition(final Node other) throws DOMException
    {
        Node parent = getParentNode();
        if (!(other instanceof NodeImpl))
        {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Unknwon node implementation");
        }
        if (parent != null && parent == other.getParentNode())
        {
            int thisIndex = getNodeIndex();
            int otherIndex = ((NodeImpl) other).getNodeIndex();
            if (thisIndex == -1 || otherIndex == -1)
            {
                return Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC;
            }
            if (thisIndex < otherIndex)
            {
                return Node.DOCUMENT_POSITION_FOLLOWING;
            }
            else
            {
                return Node.DOCUMENT_POSITION_PRECEDING;
            }
        }
        else if (isAncestorOf(other))
        {
            return Node.DOCUMENT_POSITION_CONTAINED_BY;
        }
        else if (((NodeImpl) other).isAncestorOf(this))
        {
            return Node.DOCUMENT_POSITION_CONTAINS;
        }
        else
        {
            return Node.DOCUMENT_POSITION_DISCONNECTED;
        }
    }

    /**
     * Should create a node with some cloned properties, like the node name, but not attributes or children.
     */
    protected abstract Node createSimilarNode();

    public boolean equalAttributes(final Node arg)
    {
        return false;
    }

    /**
     * Extracts all descendents that match the filter, except those descendents of nodes that match the filter.
     * 
     * @param filter
     * @param al
     */
    private void extractDescendentsArrayImpl(final NodeFilter filter, final ArrayList al)
    {
        ArrayList nl = nodeList;
        if (nl != null)
        {
            Iterator i = nl.iterator();
            while (i.hasNext())
            {
                NodeImpl n = (NodeImpl) i.next();
                if (filter.accept(n))
                {
                    al.add(n);
                }
                else if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    n.extractDescendentsArrayImpl(filter, al);
                }
            }
        }
    }

    /**
     * Tries to get a UINode associated with the current node. Failing that, it tries ancestors recursively.
     */
    public UINode findUINode()
    {
        // Called in GUI thread always.
        UINode uiNode = this.uiNode;
        if (uiNode != null)
        {
            return uiNode;
        }
        NodeImpl parentNode = (NodeImpl) getParentNode();
        return parentNode == null ? null : parentNode.findUINode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#getAlignmentX()
     */
    public float getAlignmentX()
    {
        // TODO: Removable method?
        return 0.5f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#getAlignmentY()
     */
    public float getAlignmentY()
    {
        return 0.5f;
    }

    @Override
    public NamedNodeMap getAttributes()
    {
        return null;
    }

    @Override
    public String getBaseURI()
    {
        if (document == null)
        {
            return baseURI;
        }

        return document.getBaseURI();
    }

    Node getChildAtIndex(final int index)
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            try
            {
                return nl == null ? null : (Node) nl.get(index);
            }
            catch (IndexOutOfBoundsException iob)
            {
                this.warn("getChildAtIndex(): Bad index=" + index + " for node=" + this + ".");
                return null;
            }
        }
    }

    int getChildCount()
    {
        ArrayList nl = nodeList;
        synchronized (treeLock)
        {
            return nl == null ? 0 : nl.size();
        }
    }

    int getChildIndex(final Node child)
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            return nl == null ? -1 : nl.indexOf(child);
        }
    }

    @Override
    public NodeList getChildNodes()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            return new NodeListImpl(nl == null ? Collections.EMPTY_LIST : nl, transactionId);
        }
    }

    public ChildHTMLCollection getChildren()
    {
        // Method required by JavaScript
        synchronized (this)
        {
            ChildHTMLCollection collection = childrenCollection;
            if (collection == null)
            {
                collection = new ChildHTMLCollection(this, transactionId);
                childrenCollection = collection;
            }
            return collection;
        }
    }

    public NodeImpl[] getChildrenArray()
    {
        ArrayList nl = nodeList;
        synchronized (treeLock)
        {
            return nl == null ? null : (NodeImpl[]) nl.toArray(NodeImpl.EMPTY_ARRAY);
        }
    }

    /**
     * Gets descendent nodes that match according to the filter, but it does not nest into matching nodes.
     */
    public ArrayList getDescendents(final NodeFilter filter)
    {
        ArrayList al = new ArrayList();
        synchronized (treeLock)
        {
            extractDescendentsArrayImpl(filter, al);
        }
        return al;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#getDocumentItem(java.lang.String)
     */
    @Override
    public Object getDocumentItem(final String name)
    {
        org.w3c.dom.Document document = this.document;
        return document == null ? null : document.getUserData(name);
    }

    public URL getDocumentURL()
    {
        Object doc = document;
        if (doc instanceof HTMLDocumentImpl)
        {
            return ((HTMLDocumentImpl) doc).getDocumentURL();
        }
        else
        {
            return null;
        }
    }

    @Override
    public Object getFeature(final String feature, final String version)
    {
        // TODO What should this do?
        return null;
    }

    @Override
    public Node getFirstChild()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            try
            {
                return nl == null ? null : (Node) nl.get(0);
            }
            catch (IndexOutOfBoundsException iob)
            {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#getFullURL(java.lang.String)
     */
    @Override
    public URL getFullURL(final String spec) throws MalformedURLException
    {
        Object doc = document;
        if (doc instanceof HTMLDocumentImpl)
        {
            return ((HTMLDocumentImpl) doc).getFullURL(spec);
        }
        else
        {
            return new java.net.URL(spec);
        }
    }

    public HtmlRendererContext getHtmlRendererContext()
    {
        Object doc = document;
        if (doc instanceof HTMLDocumentImpl)
        {
            return ((HTMLDocumentImpl) doc).getHtmlRendererContext();
        }
        else
        {
            return null;
        }
    }

    @Override
    public Node getLastChild()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            try
            {
                return nl == null ? null : (Node) nl.get(nl.size() - 1);
            }
            catch (IndexOutOfBoundsException iob)
            {
                return null;
            }
        }
    }

    @Override
    public abstract String getLocalName();

    @Override
    public String getNamespaceURI()
    {
        return null;
    }

    @Override
    public Node getNextSibling()
    {
        NodeImpl parent = (NodeImpl) getParentNode();
        return parent == null ? null : parent.getNextTo(this);
    }

    private Node getNextTo(final Node node)
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            int idx = nl == null ? -1 : nl.indexOf(node);
            if (idx == -1)
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "node not found");
            }
            try
            {
                return (Node) nl.get(idx + 1);
            }
            catch (IndexOutOfBoundsException iob)
            {
                return null;
            }
        }
    }

    private int getNodeIndex()
    {
        NodeImpl parent = (NodeImpl) getParentNode();
        return parent == null ? -1 : parent.getChildIndex(this);
    }

    protected NodeList getNodeList(final NodeFilter filter)
    {
        Collection collection = new ArrayList();
        synchronized (treeLock)
        {
            appendChildrenToCollectionImpl(filter, collection);
        }
        return new NodeListImpl(collection, transactionId);
    }

    @Override
    public abstract String getNodeName();

    @Override
    public abstract short getNodeType();

    @Override
    public abstract String getNodeValue() throws DOMException;

    @Override
    public Document getOwnerDocument()
    {
        return document;
    }

    @Override
    public final ModelNode getParentModelNode()
    {
        return (ModelNode) parentNode;
    }

    @Override
    public Node getParentNode()
    {
        // Should it be synchronized? Could have side-effects.
        return parentNode;
    }

    @Override
    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public Node getPreviousSibling()
    {
        NodeImpl parent = (NodeImpl) getParentNode();
        return parent == null ? null : parent.getPreviousTo(this);
    }

    private Node getPreviousTo(final Node node)
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            int idx = nl == null ? -1 : nl.indexOf(node);
            if (idx == -1)
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "node not found");
            }
            try
            {
                return (Node) nl.get(idx - 1);
            }
            catch (IndexOutOfBoundsException iob)
            {
                return null;
            }
        }
    }

    public ArrayList<NodeImpl> getRawNodeList()
    {
        return nodeList;
    }

    /**
     * Gets the text content of this node and its descendents.
     */
    @Override
    public String getTextContent() throws DOMException
    {
        StringBuffer sb = new StringBuffer();
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl != null)
            {
                Iterator i = nl.iterator();
                while (i.hasNext())
                {
                    Node node = (Node) i.next();
                    short type = node.getNodeType();
                    switch (type)
                    {
                        case Node.CDATA_SECTION_NODE:
                        case Node.TEXT_NODE:
                        case Node.ELEMENT_NODE:
                            String textContent = node.getTextContent();
                            if (textContent != null)
                            {
                                sb.append(textContent);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return sb.toString();
    }

    public UINode getUINode()
    {
        // Called in GUI thread always.
        return uiNode;
    }

    public UserAgentContext getUserAgentContext()
    {
        Object doc = document;
        if (doc instanceof HTMLDocumentImpl)
        {
            return ((HTMLDocumentImpl) doc).getUserAgentContext();
        }
        else
        {
            return null;
        }
    }

    @Override
    public Object getUserData(final String key)
    {
        synchronized (this)
        {
            Map ud = userData;
            return ud == null ? null : ud.get(key);
        }
    }

    @Override
    public boolean hasAttributes()
    {
        return false;
    }

    @Override
    public boolean hasChildNodes()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            return nl != null && !nl.isEmpty();
        }
    }

    protected void informExternalScriptLoading()
    {
        // This is called when an attribute or child changes.
        // this.forgetRenderState();
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.externalScriptLoading(this);
        }
    }

    public void informInvalid()
    {
        // This is called when an attribute or child changes.
        // this.forgetRenderState();
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.invalidated(this);
        }
    }

    public void informLayoutInvalid()
    {
        // This is called by the style properties object.
        // this.forgetRenderState();
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.invalidated(this);
        }
    }

    public void informLookInvalid()
    {
        // this.forgetRenderState();
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.lookInvalidated(this);
        }
    }

    protected void informNodeLoaded()
    {
        // This is called when an attribute or child changes.
        // this.forgetRenderState();
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.nodeLoaded(this);
        }
    }

    public void informPositionInvalid()
    {
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.positionInParentInvalidated(this);
        }
    }

    public void informSizeInvalid()
    {
        HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
        if (doc != null)
        {
            doc.sizeInvalidated(this);
        }
    }

    public Node insertAfter(final Node newChild, final Node refChild)
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            int idx = nl == null ? -1 : nl.indexOf(refChild);
            if (idx == -1)
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "refChild not found");
            }
            nl.add(idx + 1, newChild);
            if (newChild instanceof NodeImpl)
            {
                ((NodeImpl) newChild).setParentImpl(this);
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return newChild;
    }

    protected Node insertAt(final Node newChild, final int idx) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl == null)
            {
                nl = new ArrayList();
                nodeList = nl;
            }
            nl.add(idx, newChild);
            if (newChild instanceof NodeImpl)
            {
                ((NodeImpl) newChild).setParentImpl(this);
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return newChild;
    }

    @Override
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            int idx = nl == null ? -1 : nl.indexOf(refChild);
            if (idx == -1)
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "refChild not found");
            }
            nl.add(idx, newChild);
            if (newChild instanceof NodeImpl)
            {
                ((NodeImpl) newChild).setParentImpl(this);
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return newChild;
    }

    private boolean isAncestorOf(final Node other)
    {
        NodeImpl parent = (NodeImpl) other.getParentNode();
        if (parent == this)
        {
            return true;
        }
        else if (parent == null)
        {
            return false;
        }
        else
        {
            return isAncestorOf(parent);
        }
    }

    @Override
    public boolean isDefaultNamespace(final String namespaceURI)
    {
        return namespaceURI == null;
    }

    @Override
    public boolean isEqualNode(final Node arg)
    {
        return arg instanceof NodeImpl && getNodeType() == arg.getNodeType() && Objects.equals(getNodeName(), arg.getNodeName()) && Objects.equals(getNodeValue(), arg.getNodeValue()) && Objects.equals(getLocalName(), arg.getLocalName())
                        && Objects.equals(nodeList, ((NodeImpl) arg).nodeList) && equalAttributes(arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#isEqualOrDescendentOf(org.xamjwg.html.renderer.RenderableContext)
     */
    @Override
    public final boolean isEqualOrDescendentOf(final ModelNode otherContext)
    {
        if (otherContext == this)
        {
            return true;
        }
        Object parent = getParentNode();
        if (parent instanceof HTMLElementImpl)
        {
            return ((HTMLElementImpl) parent).isEqualOrDescendentOf(otherContext);
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isSameNode(final Node other)
    {
        return this == other;
    }

    @Override
    public boolean isSupported(final String feature, final String version)
    {
        return "HTML".equals(feature) && version.compareTo("4.01") <= 0;
    }

    @Override
    public String lookupNamespaceURI(final String prefix)
    {
        return null;
    }

    @Override
    public String lookupPrefix(final String namespaceURI)
    {
        return null;
    }

    @Override
    public void normalize()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl != null)
            {
                Iterator i = nl.iterator();
                List textNodes = new LinkedList();
                boolean prevText = false;
                while (i.hasNext())
                {
                    Node child = (Node) i.next();
                    if (child.getNodeType() == Node.TEXT_NODE)
                    {
                        if (!prevText)
                        {
                            prevText = true;
                            textNodes.add(child);
                        }
                    }
                    else
                    {
                        prevText = false;
                    }
                }
                i = textNodes.iterator();
                while (i.hasNext())
                {
                    Text text = (Text) i.next();
                    this.replaceAdjacentTextNodes(text);
                }
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    protected void removeAllChildren()
    {
        synchronized (treeLock)
        {
            removeAllChildrenImpl();
        }
    }

    protected void removeAllChildrenImpl()
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl != null)
            {
                nl.clear();
                // this.nodeList = null;
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    @Override
    public Node removeChild(final Node oldChild) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            if (nl == null || !nl.remove(oldChild))
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found");
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return oldChild;
    }

    // ----- ModelNode implementation

    public Node removeChildAt(final int index) throws DOMException
    {
        try
        {
            synchronized (treeLock)
            {
                ArrayList nl = nodeList;
                if (nl == null)
                {
                    throw new DOMException(DOMException.INDEX_SIZE_ERR, "Empty list of children");
                }
                Node n = (Node) nl.remove(index);
                if (n == null)
                {
                    throw new DOMException(DOMException.INDEX_SIZE_ERR, "No node with that index");
                }
                return n;
            }
        }
        finally
        {
            if (!notificationsSuspended)
            {
                informInvalid();
            }
        }
    }

    protected void removeChildren(final NodeFilter filter)
    {
        synchronized (treeLock)
        {
            removeChildrenImpl(filter);
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    protected void removeChildrenImpl(final NodeFilter filter)
    {
        ArrayList nl = nodeList;
        if (nl != null)
        {
            int len = nl.size();
            for (int i = len; --i >= 0;)
            {
                Node node = (Node) nl.get(i);
                if (filter.accept(node))
                {
                    nl.remove(i);
                }
            }
        }
    }

    public Text replaceAdjacentTextNodes(final Text node)
    {
        try
        {
            synchronized (treeLock)
            {
                ArrayList nl = nodeList;
                if (nl == null)
                {
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "Node not a child");
                }
                int idx = nl.indexOf(node);
                if (idx == -1)
                {
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "Node not a child");
                }
                StringBuffer textBuffer = new StringBuffer();
                int firstIdx = idx;
                List toDelete = new LinkedList();
                for (int adjIdx = idx; --adjIdx >= 0;)
                {
                    Object child = nodeList.get(adjIdx);
                    if (child instanceof Text)
                    {
                        firstIdx = adjIdx;
                        toDelete.add(child);
                        textBuffer.append(((Text) child).getNodeValue());
                    }
                }
                int length = nodeList.size();
                for (int adjIdx = idx; ++adjIdx < length;)
                {
                    Object child = nodeList.get(adjIdx);
                    if (child instanceof Text)
                    {
                        toDelete.add(child);
                        textBuffer.append(((Text) child).getNodeValue());
                    }
                }
                nodeList.removeAll(toDelete);
                TextImpl textNode = new TextImpl(textBuffer.toString(), transactionId);
                textNode.setOwnerDocument(document);
                textNode.setParentImpl(this);
                nodeList.add(firstIdx, textNode);
                return textNode;
            }
        }
        finally
        {
            if (!notificationsSuspended)
            {
                informInvalid();
            }
        }
    }

    public Text replaceAdjacentTextNodes(final Text node, final String textContent)
    {
        try
        {
            synchronized (treeLock)
            {
                ArrayList nl = nodeList;
                if (nl == null)
                {
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "Node not a child");
                }
                int idx = nl.indexOf(node);
                if (idx == -1)
                {
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "Node not a child");
                }
                int firstIdx = idx;
                List toDelete = new LinkedList();
                for (int adjIdx = idx; --adjIdx >= 0;)
                {
                    Object child = nodeList.get(adjIdx);
                    if (child instanceof Text)
                    {
                        firstIdx = adjIdx;
                        toDelete.add(child);
                    }
                }
                int length = nodeList.size();
                for (int adjIdx = idx; ++adjIdx < length;)
                {
                    Object child = nodeList.get(adjIdx);
                    if (child instanceof Text)
                    {
                        toDelete.add(child);
                    }
                }
                nodeList.removeAll(toDelete);
                TextImpl textNode = new TextImpl(textContent, transactionId);
                textNode.setOwnerDocument(document);
                textNode.setParentImpl(this);
                nodeList.add(firstIdx, textNode);
                return textNode;
            }
        }
        finally
        {
            if (!notificationsSuspended)
            {
                informInvalid();
            }
        }
    }

    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException
    {
        synchronized (treeLock)
        {
            ArrayList nl = nodeList;
            int idx = nl == null ? -1 : nl.indexOf(oldChild);
            if (idx == -1)
            {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "oldChild not found");
            }
            nl.set(idx, newChild);
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
        return newChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.renderer.RenderableContext#setDocumentItem(java.lang.String, java.lang.Object)
     */
    @Override
    public void setDocumentItem(final String name, final Object value)
    {
        org.w3c.dom.Document document = this.document;
        if (document == null)
        {
            return;
        }
        document.setUserData(name, value, null);
    }

    @Override
    public abstract void setNodeValue(String nodeValue) throws DOMException;

    void setOwnerDocument(final Document value)
    {
        document = value;
        treeLock = value == null ? this : (Object) value;
    }

    void setOwnerDocument(final Document value, final boolean deep)
    {
        document = value;
        treeLock = value == null ? this : (Object) value;
        if (deep)
        {
            synchronized (treeLock)
            {
                ArrayList nl = nodeList;
                if (nl != null)
                {
                    Iterator i = nl.iterator();
                    while (i.hasNext())
                    {
                        NodeImpl child = (NodeImpl) i.next();
                        child.setOwnerDocument(value, deep);
                    }
                }
            }
        }
    }

    final void setParentImpl(final Node parent)
    {
        // Call holding treeLock.
        parentNode = parent;
    }

    @Override
    public void setPrefix(final String prefix) throws DOMException
    {
        this.prefix = prefix;
    }

    public void setRawNodeList(final ArrayList<NodeImpl> nodeList)
    {
        this.nodeList = nodeList;
    }

    @Override
    public void setTextContent(final String textContent) throws DOMException
    {
        synchronized (treeLock)
        {
            removeChildrenImpl(new TextFilter());
            if (textContent != null && !"".equals(textContent))
            {
                TextImpl t = new TextImpl(textContent, transactionId);
                t.setOwnerDocument(document);
                t.setParentImpl(this);
                ArrayList nl = nodeList;
                if (nl == null)
                {
                    nl = new ArrayList();
                    nodeList = nl;
                }
                nl.add(t);
            }
        }
        if (!notificationsSuspended)
        {
            informInvalid();
        }
    }

    public void setUINode(final UINode uiNode)
    {
        // Called in GUI thread always.
        this.uiNode = uiNode;
    }

    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler)
    {
        if (org.cobra_grendel.html.parser.HtmlParser.MODIFYING_KEY.equals(key))
        {
            boolean ns = Boolean.TRUE == data;
            notificationsSuspended = ns;
            if (!ns)
            {
                informNodeLoaded();
            }
        }
        synchronized (this)
        {
            if (handler != null)
            {
                if (userDataHandlers == null)
                {
                    userDataHandlers = new LinkedList();
                }
                userDataHandlers.add(handler);
            }
            Map userData = this.userData;
            if (userData == null)
            {
                userData = new HashMap();
                this.userData = userData;
            }
            return userData.put(key, data);
        }
    }

    @Override
    public String toString()
    {
        return getNodeName();
    }

    void visit(final NodeVisitor visitor)
    {
        synchronized (treeLock)
        {
            visitImpl(visitor);
        }
    }

    void visitImpl(final NodeVisitor visitor)
    {
        try
        {
            visitor.visit(this);
        }
        catch (SkipVisitorException sve)
        {
            return;
        }
        catch (StopVisitorException sve)
        {
            throw sve;
        }
        ArrayList nl = nodeList;
        if (nl != null)
        {
            Iterator i = nl.iterator();
            while (i.hasNext())
            {
                NodeImpl child = (NodeImpl) i.next();
                try
                {
                    // Call with child's synchronization
                    child.visit(visitor);
                }
                catch (StopVisitorException sve)
                {
                    throw sve;
                }
            }
        }
    }

    public void warn(final String message)
    {
        logger.log(Level.WARNING, message);
    }

    @Override
    public void warn(final String message, final Throwable err)
    {
        logger.log(Level.WARNING, message, err);
    }

    // private RenderState renderState = INVALID_RENDER_STATE;
    //
    // public RenderState getRenderState() {
    // // Generally called from the GUI thread, except for
    // // offset properties.
    // RenderState rs;
    // synchronized(this.treeLock) {
    // rs = this.renderState;
    // if(rs != INVALID_RENDER_STATE) {
    // return rs;
    // }
    // Object parent = this.parentNode;
    // if(parent != null || this instanceof Document) {
    // RenderState prs = this.getParentRenderState(parent);
    // rs = this.createRenderState(prs);
    // this.renderState = rs;
    // return rs;
    // }
    // else {
    // // Return null without caching.
    // // Scenario is possible due to Javascript.
    // return null;
    // }
    // }
    // }
    //
    // protected final RenderState getParentRenderState(Object parent) {
    // if(parent instanceof NodeImpl) {
    // return ((NodeImpl) parent).getRenderState();
    // }
    // else {
    // return null;
    // }
    // }
    //
    // protected RenderState createRenderState(RenderState prevRenderState) {
    // return prevRenderState;
    // }
    //
    // protected void forgetRenderState() {
    // synchronized(this.treeLock) {
    // if(this.renderState != INVALID_RENDER_STATE) {
    // this.renderState = INVALID_RENDER_STATE;
    // // Note that getRenderState() "validates"
    // // ancestor states as well.
    // java.util.ArrayList nl = this.nodeList;
    // if(nl != null) {
    // Iterator i = nl.iterator();
    // while(i.hasNext()) {
    // ((NodeImpl) i.next()).forgetRenderState();
    // }
    // }
    // }
    // }
    // }

}
