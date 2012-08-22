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
 * Created on Jan 14, 2006
 */
package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.FormInput;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLFormElement;

import com.grendelscan.logging.Log;

public class HTMLFormElementImpl extends HTMLAbstractUIElement implements HTMLFormElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private class InputFilter implements NodeFilter
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xamjwg.html.domimpl.NodeFilter#accept(org.w3c.dom.Node)
		 */
		@Override public boolean accept(Node node)
		{
			return HTMLFormElementImpl.isInput(node);
		}
	}
	
	private Function onsubmit;
	
	public HTMLFormElementImpl(int transactionId)
	{
		super("FORM", transactionId);
	}
	
	public HTMLFormElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	static boolean isInput(Node node)
	{
		String name = node.getNodeName().toLowerCase();
		return name.equals("input") || name.equals("textarea") || name.equals("select");
	}
	
	@Override public String getAcceptCharset()
	{
		return getAttribute("acceptCharset");
	}
	
	@Override public String getAction()
	{
		return getAttribute("action");
	}
	
	@Override public HTMLCollection getElements()
	{
		return new DescendentHTMLCollection(this, new InputFilter(), transactionId);
	}
	
	@Override public String getEnctype()
	{
		return getAttribute("enctype");
	}
	
	@Override public int getLength()
	{
		return new DescendentHTMLCollection(this, new InputFilter(), transactionId).getLength();
	}
	
	@Override public String getMethod()
	{
		String method = getAttribute("method");
		if (method == null)
		{
			method = "GET";
		}
		return method;
	}
	
	@Override public String getName()
	{
		return getAttribute("name");
	}
	
	public Function getOnsubmit()
	{
		return getEventFunction(onsubmit, "onsubmit");
	}
	
	@Override public String getTarget()
	{
		return getAttribute("target");
	}
	
	public Object item(final int index)
	{
		try
		{
			visit(new NodeVisitor()
			{
				private int current = 0;
				
				@Override public void visit(Node node)
				{
					if (HTMLFormElementImpl.isInput(node))
					{
						if (current == index)
						{
							throw new StopVisitorException(node);
						}
						current++;
					}
				}
			});
		}
		catch (StopVisitorException sve)
		{
			return sve.getTag();
		}
		return null;
	}
	
	public Object namedItem(final String name)
	{
		try
		{
			// TODO: This could use document.namedItem.
			visit(new NodeVisitor()
			{
				@Override public void visit(Node node)
				{
					if (HTMLFormElementImpl.isInput(node))
					{
						if (name.equals(((Element) node).getAttribute("name")))
						{
							throw new StopVisitorException(node);
						}
					}
				}
			});
		}
		catch (StopVisitorException sve)
		{
			return sve.getTag();
		}
		return null;
	}
	
	@Override public void reset()
	{
		visit(new NodeVisitor()
		{
			@Override public void visit(Node node)
			{
				if (node instanceof HTMLBaseInputElement)
				{
					((HTMLBaseInputElement) node).resetInput();
				}
			}
		});
	}
	
	@Override public void setAcceptCharset(String acceptCharset)
	{
		setAttribute("acceptCharset", acceptCharset);
	}
	
	@Override public void setAction(String action)
	{
		setAttribute("action", action);
	}
	
	@Override public void setEnctype(String enctype)
	{
		setAttribute("enctype", enctype);
	}
	
	@Override public void setMethod(String method)
	{
		setAttribute("method", method);
	}
	
	@Override public void setName(String name)
	{
		setAttribute("name", name);
	}
	
	public void setOnsubmit(Function value)
	{
		onsubmit = value;
	}
	
	@Override public void setTarget(String target)
	{
		setAttribute("target", target);
	}
	
	@Override public void submit()
	{
		this.submit(null);
	}
	
	/**
	 * This method should be called when form submission is done by a submit
	 * button.
	 * 
	 * @param extraFormInputs
	 *            Any additional form inputs that need to be submitted, e.g. the
	 *            submit button parameter.
	 */
	public final void submit(final FormInput[] extraFormInputs)
	{
		Log.warn("Something tried to submit a form to " + getAction());
	}
	
}
