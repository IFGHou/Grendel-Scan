/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: xamjadmin@users.sourceforge.net
*/

package com.grendelscan.html;

import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.html.domimpl.HTMLElementImpl;
import org.cobra_grendel.html.domimpl.HTMLHtmlElementImpl;
import org.cobra_grendel.html.js.Executor;
import org.cobra_grendel.js.JavaScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Cobbled together from various parts of Cobra and the sample DOM writer in
 * Xerces by Andy Clark.
 * @author David Byrne
 *
 */
public class EventHandlerExecutor
{
	/**
	 * A major limitation is that all events are execute against the same DOM. This
	 * is due to the difficulty of DOM cloning. This won't be a problem for most pages,
	 * but if a page modifies the page in a way that prevents other events from running
	 * properly, the desired results won't be obtained.
	 * @param node
	 * @param terminationCondition
	 */
	public static void executeEvents(Node node, EventExecutorTerminator terminationCondition)
	{
		internalExecuteEvents(node, terminationCondition);
	}

	/**
	 * Returns true if the test is terminated
	 * @param node
	 * @param terminationCondition
	 * @return
	 */
	private static boolean internalExecuteEvents(Node node, EventExecutorTerminator terminationCondition)
	{
		boolean terminate = false;
		// is there anything to do?
		if (node == null)
		{
			return terminate;
		}

		short type = node.getNodeType();
		switch (type)
		{
			case Node.DOCUMENT_NODE:
			{
				Document document = (Document) node;
				Element e = document.getDocumentElement();
				if (! (e instanceof HTMLHtmlElementImpl))
				{
					// This is in case there is no HTML tag. Some of the DOM can still be salvaged.
					Node child = document.getFirstChild();
					while (child != null)
					{
						terminate = internalExecuteEvents(child, terminationCondition);
						if (terminate)
						{
							break;
						}
						child = child.getNextSibling();
					}
				}
				else
				{
					terminate = internalExecuteEvents(e, terminationCondition);
				}
				break;
			}

			
			case Node.ELEMENT_NODE:
			{
				NamedNodeMap attrs = node.getAttributes();
				for (int index = 0; index < attrs.getLength(); index++)
				{
					Node attr = attrs.item(index);
					if (attr.getNodeName().toLowerCase().startsWith("on"))
					{
						executeAttributeValue((HTMLElementImpl) node, attr.getNodeName());
						terminate = terminationCondition.stopExecution(node);
						if (terminate)
						{
							break;
						}
					}
				}
				
				if (terminate)
				{
					break;
				}
				
				Node child = node.getFirstChild();
				while (child != null)
				{
					terminate = internalExecuteEvents(child, terminationCondition);
					if (terminate)
					{
						break;
					}
					child = child.getNextSibling();
				}
				break;
			}

			case Node.ENTITY_REFERENCE_NODE:
			{
				Node child = node.getFirstChild();
				while (child != null)
				{
					terminate = internalExecuteEvents(child, terminationCondition);
					if (terminate)
					{
						break;
					}
					child = child.getNextSibling();
				}
				break;
			}

		}
		return terminate;
	} 

	
	
	private static void executeAttributeValue(HTMLElementImpl element, String attributeName)
	{
		String attributeValue = element.getAttributes().getNamedItem(attributeName).getNodeValue();
		if (attributeValue != null && ! attributeValue.equals(""))
		{
			String functionCode =
		        "function " + attributeName + "_" + System.identityHashCode(element) + "() { " + attributeValue
		                + " }";
			HTMLDocumentImpl document = (HTMLDocumentImpl) element.getOwnerDocument();
			if (document != null)
			{
				Scriptable scope = (Scriptable) document.getUserData(Executor.SCOPE_KEY);
				Scriptable thisScope = (Scriptable) JavaScript.getInstance().getJavascriptObject(element, scope);
				Context ctx = Executor.createContext(document.getDocumentURL(), document.getUserAgentContext());
				try
				{
					Function eventFunction =
					        ctx.compileFunction(thisScope, functionCode, element.getTagName() + "[" + element.getId()
					                + "]." + attributeName, 1, null);
					Executor.executeFunction(document, eventFunction, null);
				}
				catch (EcmaError ecmaError)
				{
					element.warn("Javascript error at " + ecmaError.getSourceName() + ":" + ecmaError.getLineNumber()
					        + ": " + ecmaError.getMessage());
				}
				catch (Throwable err)
				{
					element.warn("Unable to evaluate Javascript code", err);
				}
				finally
				{
					Context.exit();
				}
			}
		}
	}
	
}
