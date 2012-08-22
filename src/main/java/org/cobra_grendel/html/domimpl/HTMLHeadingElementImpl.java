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
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.w3c.dom.html2.HTMLHeadingElement;

public class HTMLHeadingElementImpl extends HTMLAbstractUIElement implements HTMLHeadingElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLHeadingElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public String getAlign()
	{
		return getAttribute("align");
	}
	
	@Override public void setAlign(String align)
	{
		setAttribute("align", align);
	}
	
	private final float getHeadingFontSize()
	{
		String tagName = getTagName();
		try
		{
			int lastCharValue = tagName.charAt(1) - '0';
			switch (lastCharValue)
			{
				case 1:
					return 24.0f;
				case 2:
					return 18.0f;
				case 3:
					return 15.0f;
				case 4:
					return 12.0f;
				case 5:
					return 10.0f;
				case 6:
					return 8.0f;
			}
		}
		catch (Exception thrown)
		{
			this.warn("getHeadingFontSize(): Bad heading tag: " + getTagName(), thrown);
		}
		return 14.0f;
	}
	
	// protected RenderState createRenderState(RenderState prevRenderState) {
	// float fontSize = this.getHeadingFontSize();
	// prevRenderState = new FontSizeRenderState(prevRenderState, fontSize,
	// java.awt.Font.BOLD);
	// return new BlockRenderState(prevRenderState, this);
	// }
}
