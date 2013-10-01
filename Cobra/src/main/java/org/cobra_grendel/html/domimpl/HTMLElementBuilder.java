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

import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;

public abstract class HTMLElementBuilder
{
	public static class Anchor extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLLinkElementImpl(name, transactionId);
		}
	}
	
	public static class Applet extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLAppletElementImpl(name, transactionId);
		}
	}
	
	public static class Base extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLBaseElementImpl(name, transactionId);
		}
	}
	
	public static class BaseFont extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLBaseFontElementImpl(name, transactionId);
		}
	}
	
	public static class Big extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFontSizeChangeElementImpl(name, +1, transactionId);
		}
	}
	
	public static class Blockquote extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLBlockQuoteElementImpl(name, transactionId);
		}
	}
	
	public static class Body extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLBodyElementImpl(name, transactionId);
		}
	}
	
	public static class Br extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLBRElementImpl(name, transactionId);
		}
	}
	
	public static class Button extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLButtonElementImpl(name, transactionId);
		}
	}
	
	public static class Center extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLCenterElementImpl(name, transactionId);
		}
	}
	
	public static class Code extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLMonospacedElementImpl(name, transactionId);
		}
	}
	
	public static class Div extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLDivElementImpl(name, transactionId);
		}
	}
	
	public static class Em extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLEmElementImpl(name, transactionId);
		}
	}
	
	public static class Font extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFontElementImpl(name, transactionId);
		}
	}
	
	public static class Form extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFormElementImpl(name, transactionId);
		}
	}
	
	public static class Frame extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFrameElementImpl(name, transactionId);
		}
	}
	
	public static class Frameset extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFrameSetElementImpl(name, transactionId);
		}
	}
	
/* TODO UCdetector: Remove unused code: 
	public static class GenericMarkup extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, transactionId)
		{
			return new HTMLGenericMarkupElement(name, transactionId);
		}
	}
*/
	
	public static class Heading extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLHeadingElementImpl(name, transactionId);
		}
	}
	
	public static class Hr extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLHRElementImpl(name, transactionId);
		}
	}
	
	public static class Html extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLHtmlElementImpl(name, transactionId);
		}
	}
	
	public static class HtmlObject extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLObjectElementImpl(name, transactionId);
		}
	}
	
	public static class IFrame extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLIFrameElementImpl(name, transactionId);
		}
	}
	
	public static class Img extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLImageElementImpl(name, transactionId);
		}
	}
	
	public static class Input extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLInputElementImpl(name, transactionId);
		}
	}
	
	public static class Li extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLLIElementImpl(name, transactionId);
		}
	}
	
	public static class Link extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLLinkElementImpl(name, transactionId);
		}
	}
	
	public static class NonStandard extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLNonStandardElement(name, transactionId);
		}
	}
	
	public static class Ol extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLOListElementImpl(name, transactionId);
		}
	}
	
	public static class Option extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLOptionElementImpl(name, transactionId);
		}
	}
	
	public static class P extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLPElementImpl(name, transactionId);
		}
	}
	
	public static class Pre extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLPreElementImpl(name, transactionId);
		}
	}
	
	public static class Script extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLScriptElementImpl(name, transactionId);
		}
	}
	
	public static class Select extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLSelectElementImpl(name, transactionId);
		}
	}
	
	public static class Small extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLFontSizeChangeElementImpl(name, -1, transactionId);
		}
	}
	
	public static class Span extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLSpanElementImpl(name, transactionId);
		}
	}
	
	public static class Strike extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLStrikeElementImpl(name, transactionId);
		}
	}
	
	public static class Strong extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLStrongElementImpl(name, transactionId);
		}
	}
	
	public static class Style extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLStyleElementImpl(name, transactionId);
		}
	}
	
	public static class Table extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTableElementImpl(name, transactionId);
		}
	}
	
	public static class Td extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTableCellElementImpl(name, transactionId);
		}
	}
	
	public static class Textarea extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTextAreaElementImpl(name, transactionId);
		}
	}
	
	public static class Th extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTableHeadElementImpl(name, transactionId);
		}
	}
	
	public static class Title extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTitleElementImpl(name, transactionId);
		}
	}
	
	public static class Tr extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLTableRowElementImpl(name, transactionId);
		}
	}
	
	public static class Tt extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLMonospacedElementImpl(name, transactionId);
		}
	}
	
	public static class Ul extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLUListElementImpl(name, transactionId);
		}
	}
	
	public static class Underline extends HTMLElementBuilder
	{
		@Override
		public HTMLElementImpl build(String name, int transactionId)
		{
			return new HTMLUnderlineElementImpl(name, transactionId);
		}
	}
	
	public final HTMLElement create(HTMLDocument document, String name, int transactionId)
	{
		HTMLElementImpl element = build(name, transactionId);
		element.setOwnerDocument(document);
		return element;
	}
	
	protected abstract HTMLElementImpl build(String name, int transactionId);
}
