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

// import org.cobra_grendel.html.style.BaseFontRenderState;
// import org.cobra_grendel.html.style.ColorRenderState;
// import org.cobra_grendel.html.style.FontNameRenderState;
// import org.cobra_grendel.html.style.FontSizeRenderState;
// import org.cobra_grendel.html.style.HtmlValues;
// import org.cobra_grendel.html.style.RenderState;
// import org.cobra_grendel.util.gui.ColorFactory;
import org.w3c.dom.html2.HTMLFontElement;

public class HTMLFontElementImpl extends HTMLAbstractUIElement implements HTMLFontElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLFontElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public String getColor()
	{
		return getAttribute("color");
	}
	
	@Override public String getFace()
	{
		return getAttribute("face");
	}
	
	@Override public String getSize()
	{
		return getAttribute("size");
	}
	
	@Override public void setColor(String color)
	{
		setAttribute("color", color);
	}
	
	@Override public void setFace(String face)
	{
		setAttribute("face", face);
	}
	
	@Override public void setSize(String size)
	{
		setAttribute("size", size);
	}
	
	// protected RenderState createRenderState(RenderState prevRenderState) {
	// String face = this.getAttribute("face");
	// String size = this.getAttribute("size");
	// String color = this.getAttribute("color");
	// if(face != null) {
	// prevRenderState = new FontNameRenderState(prevRenderState, face);
	// }
	// if(size != null) {
	// int fontNumber = HtmlValues.getFontNumberOldStyle(size, prevRenderState);
	// float fontSize = HtmlValues.getFontSize(fontNumber);
	// prevRenderState = new FontSizeRenderState(prevRenderState, fontSize);
	// }
	// if(color != null) {
	// prevRenderState = new ColorRenderState(prevRenderState,
	// ColorFactory.getInstance().getColor(color));
	// }
	// return prevRenderState;
	// }
}
