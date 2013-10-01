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
package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;

/**
 * This element is used for SMALL and BIG.
 */
public class HTMLFontSizeChangeElementImpl extends HTMLAbstractUIElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final int fontChange;

    public HTMLFontSizeChangeElementImpl(final String name, final int fontChange, final int transactionId)
    {
        super(name, transactionId);
        this.fontChange = fontChange;
    }

    // protected RenderState createRenderState(RenderState prevRenderState) {
    // int fontSize;
    // if(prevRenderState == null) {
    // fontSize = 13;
    // }
    // else {
    // fontSize = prevRenderState.getFont().getSize();
    // fontSize += (this.fontChange * 2);
    // if(fontSize < 1) {
    // fontSize = 1;
    // }
    // }
    // prevRenderState = new FontSizeRenderState(prevRenderState, fontSize);
    // return super.createRenderState(prevRenderState);
    // }
}
