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
 * Created on Oct 29, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cobra_grendel.util.Objects;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class ElementImpl extends NodeImpl implements Element
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String id;
	
	private final String name;
	
	protected Map attributes;
	
	public ElementImpl(String name, int transactionId)
	{
		super(transactionId);
		this.name = name;
	}
	
	protected static boolean isTagName(Node node, String name)
	{
		return node.getNodeName().equalsIgnoreCase(name);
	}
	
	@Override
	public boolean equalAttributes(Node arg)
	{
		if (arg instanceof ElementImpl)
		{
			synchronized (this)
			{
				Map attrs1 = attributes;
				if (attrs1 == null)
				{
					attrs1 = Collections.EMPTY_MAP;
				}
				Map attrs2 = ((ElementImpl) arg).attributes;
				if (attrs2 == null)
				{
					attrs2 = Collections.EMPTY_MAP;
				}
				return Objects.equals(attrs1, attrs2);
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override public final String getAttribute(String name)
	{
		String normalName = normalizeAttributeName(name);
		synchronized (this)
		{
			Map attributes = this.attributes;
			return attributes == null ? null : (String) attributes.get(normalName);
		}
	}
	
	@Override public Attr getAttributeNode(String name)
	{
		String normalName = normalizeAttributeName(name);
		synchronized (this)
		{
			Map attributes = this.attributes;
			String value = attributes == null ? null : (String) attributes.get(normalName);
			return value == null ? null : getAttr(normalName, value);
		}
	}
	
	@Override public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	// private String title;
	
	@Override public String getAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getattributes()
	 */
	@Override
	public NamedNodeMap getAttributes()
	{
		synchronized (this)
		{
			Map attrs = attributes;
			if (attrs == null)
			{
				attrs = new HashMap();
				attributes = attrs;
			}
			return new NamedNodeMapImpl(this, attributes, transactionId);
		}
	}
	
	public String getDir()
	{
		return getAttribute("dir");
	}
	
	@Override public NodeList getElementsByTagName(String name)
	{
		boolean matchesAll = "*".equals(name);
		List descendents = new LinkedList();
		synchronized (treeLock)
		{
			ArrayList nl = nodeList;
			if (nl != null)
			{
				Iterator i = nl.iterator();
				while (i.hasNext())
				{
					Object child = i.next();
					if (child instanceof Element)
					{
						Element childElement = (Element) child;
						if (matchesAll || isTagName(childElement, name))
						{
							descendents.add(child);
						}
						NodeList sublist = childElement.getElementsByTagName(name);
						int length = sublist.getLength();
						for (int idx = 0; idx < length; idx++)
						{
							descendents.add(sublist.item(idx));
						}
					}
				}
			}
		}
		return new NodeListImpl(descendents, transactionId);
	}
	
	@Override public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	public String getId()
	{
		return id;
	}
	
	/**
	 * Attempts to convert the subtree starting at this point to a close text
	 * representation. BR elements are converted to line breaks, and so forth.
	 */
	public String getInnerText()
	{
		StringBuffer buffer = new StringBuffer();
		synchronized (treeLock)
		{
			appendInnerTextImpl(buffer);
		}
		return buffer.toString();
	}
	
	public String getLang()
	{
		return getAttribute("lang");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getLocalName()
	 */
	@Override
	public String getLocalName()
	{
		return getNodeName();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeName()
	 */
	@Override
	public String getNodeName()
	{
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeType()
	 */
	@Override
	public short getNodeType()
	{
		return Node.ELEMENT_NODE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#getNodeValue()
	 */
	@Override
	public String getNodeValue() throws DOMException
	{
		return null;
	}
	
	@Override public TypeInfo getSchemaTypeInfo()
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	@Override public String getTagName()
	{
		return getNodeName();
	}
	
	public String getTitle()
	{
		return getAttribute("title");
	}
	
	@Override public boolean hasAttribute(String name)
	{
		String normalName = normalizeAttributeName(name);
		synchronized (this)
		{
			Map attributes = this.attributes;
			return attributes == null ? false : attributes.containsKey(normalName);
		}
	}
	
	@Override public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	@Override
	public boolean hasAttributes()
	{
		synchronized (this)
		{
			Map attrs = attributes;
			return attrs == null ? false : !attrs.isEmpty();
		}
	}
	
	@Override public void removeAttribute(String name) throws DOMException
	{
		String normalName = normalizeAttributeName(name);
		synchronized (this)
		{
			Map attributes = this.attributes;
			if (attributes == null)
			{
				return;
			}
			attributes.remove(normalName);
		}
	}
	
	@Override public Attr removeAttributeNode(Attr oldAttr) throws DOMException
	{
		String normalName = normalizeAttributeName(oldAttr.getName());
		synchronized (this)
		{
			Map attributes = this.attributes;
			if (attributes == null)
			{
				return null;
			}
			String oldValue = (String) attributes.remove(normalName);
			// TODO: "specified" attributes
			return oldValue == null ? null : getAttr(normalName, oldValue);
		}
	}
	
	@Override public void removeAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	@Override public void setAttribute(String name, String value) throws DOMException
	{
		String normalName = normalizeAttributeName(name);
		synchronized (this)
		{
			Map attribs = attributes;
			if (attribs == null)
			{
				attribs = new HashMap(2);
				attributes = attribs;
			}
			attribs.put(normalName, value);
		}
		assignAttributeField(normalName, value);
	}
	
	/**
	 * Fast method to set attributes. It is not thread safe. Calling thread
	 * should hold a treeLock.
	 */
	public void setAttributeImpl(String name, String value) throws DOMException
	{
		String normalName = normalizeAttributeName(name);
		Map attribs = attributes;
		if (attribs == null)
		{
			attribs = new HashMap(2);
			attributes = attribs;
		}
		assignAttributeField(normalName, value);
		attribs.put(normalName, value);
	}
	
	@Override public Attr setAttributeNode(Attr newAttr) throws DOMException
	{
		String normalName = normalizeAttributeName(newAttr.getName());
		String value = newAttr.getValue();
		synchronized (this)
		{
			if (attributes == null)
			{
				attributes = new HashMap();
			}
			attributes.put(normalName, value);
			// this.setIdAttribute(normalName, newAttr.isId());
		}
		assignAttributeField(normalName, value);
		return newAttr;
	}
	
	@Override public Attr setAttributeNodeNS(Attr newAttr) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	@Override public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	public void setDir(String dir)
	{
		setAttribute("dir", dir);
	}
	
	public void setId(String id)
	{
		setAttribute("id", id);
	}
	
	@Override public void setIdAttribute(String name, boolean isId) throws DOMException
	{
		String normalName = normalizeAttributeName(name);
		if (!"id".equals(normalName))
		{
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "IdAttribute can't be anything other than ID");
		}
	}
	
	@Override public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException
	{
		String normalName = normalizeAttributeName(idAttr.getName());
		if (!"id".equals(normalName))
		{
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "IdAttribute can't be anything other than ID");
		}
	}
	
	@Override public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException
	{
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Namespaces not supported");
	}
	
	public void setInnerText(String newText)
	{
		org.w3c.dom.Document document = this.document;
		if (document == null)
		{
			this.warn("setInnerText(): Element " + this + " does not belong to a document.");
			return;
		}
		synchronized (treeLock)
		{
			ArrayList nl = nodeList;
			if (nl != null)
			{
				nl.clear();
			}
		}
		// Create node and call appendChild outside of synchronized block.
		Node textNode = document.createTextNode(newText);
		appendChild(textNode);
	}
	
	public void setLang(String lang)
	{
		setAttribute("lang", lang);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xamjwg.html.domimpl.NodeImpl#setNodeValue(java.lang.String)
	 */
	@Override
	public void setNodeValue(String nodeValue) throws DOMException
	{
		// nop
	}
	
	public void setTitle(String title)
	{
		setAttribute("title", title);
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getNodeName());
		sb.append(" [");
		NamedNodeMap attribs = getAttributes();
		int length = attribs.getLength();
		for (int i = 0; i < length; i++)
		{
			Attr attr = (Attr) attribs.item(i);
			sb.append(attr.getNodeName());
			sb.append('=');
			sb.append(attr.getNodeValue());
			if (i + 1 < length)
			{
				sb.append(',');
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	private Attr getAttr(String normalName, String value)
	{
		// TODO: "specified" attributes
		return new AttrImpl(normalName, value, true, this, "id".equals(normalName), transactionId);
	}
	
	protected void appendInnerTextImpl(StringBuffer buffer)
	{
		ArrayList nl = nodeList;
		if (nl == null)
		{
			return;
		}
		int size = nl.size();
		if (size == 0)
		{
			return;
		}
		for (int i = 0; i < size; i++)
		{
			Node child = (Node) nl.get(i);
			if (child instanceof ElementImpl)
			{
				((ElementImpl) child).appendInnerTextImpl(buffer);
			}
			if (child instanceof Comment)
			{
				// skip
			}
			else if (child instanceof Text)
			{
				buffer.append(((Text) child).getTextContent());
			}
		}
	}
	
	protected void assignAttributeField(String normalName, String value)
	{
		// Note: overriders assume that processing here is only done after
		// checking attribute names, i.e. they may not call the super
		// implementation if an attribute is already taken care of.
		boolean isName = false;
		if ("id".equals(normalName) || (isName = "name".equals(normalName)))
		{
			// Note that the value of name is used
			// as an ID, but the value of ID is not
			// used as a name.
			String oldId = id;
			id = value;
			HTMLDocumentImpl document = (HTMLDocumentImpl) this.document;
			if (document != null)
			{
				if (oldId != null)
				{
					document.removeElementById(oldId);
				}
				document.setElementById(value, this);
				if (isName)
				{
					String oldName = getAttribute("name");
					if (oldName != null)
					{
						document.removeNamedItem(oldName);
					}
					document.setNamedItem(value, this);
				}
			}
		}
	}
	
	@Override
	protected Node createSimilarNode()
	{
		HTMLDocumentImpl doc = (HTMLDocumentImpl) document;
		return doc == null ? null : doc.createElement(getTagName());
	}
	
	/**
	 * Gets inner text of the element, possibly including text in comments. This
	 * can be used to get Javascript code out of a SCRIPT element.
	 * 
	 * @param includeComment
	 */
	protected String getRawInnerText(boolean includeComment)
	{
		synchronized (treeLock)
		{
			ArrayList nl = nodeList;
			if (nl != null)
			{
				Iterator i = nl.iterator();
				StringBuffer sb = null;
				while (i.hasNext())
				{
					Object node = i.next();
					if (node instanceof Text)
					{
						Text tn = (Text) node;
						String txt = tn.getNodeValue();
						if (!"".equals(txt))
						{
							if (sb == null)
							{
								sb = new StringBuffer();
							}
							sb.append(txt);
						}
					}
					else if (node instanceof ElementImpl)
					{
						ElementImpl en = (ElementImpl) node;
						String txt = en.getRawInnerText(includeComment);
						if (!"".equals(txt))
						{
							if (sb == null)
							{
								sb = new StringBuffer();
							}
							sb.append(txt);
						}
					}
					else if (includeComment && (node instanceof Comment))
					{
						Comment cn = (Comment) node;
						String txt = cn.getNodeValue();
						if (!"".equals(txt))
						{
							if (sb == null)
							{
								sb = new StringBuffer();
							}
							sb.append(txt);
						}
					}
				}
				return sb == null ? "" : sb.toString();
			}
			else
			{
				return "";
			}
		}
	}
	
	protected final String normalizeAttributeName(String name)
	{
		return name.toLowerCase();
	}
}
