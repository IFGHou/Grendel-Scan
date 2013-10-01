/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The XAMJ Project
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
package org.cobra_grendel.html.js;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.w3c.dom.html2.HTMLElement;

public class Event extends AbstractScriptableDelegate
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private boolean cancelBubble;
	private HTMLElement fromElement, toElement;
	private final java.awt.event.InputEvent inputEvent;
	private int leafX, leafY;
	private boolean returnValue;
	private HTMLElement srcElement;
	private String type;
	
	public Event(String type, HTMLElement srcElement, java.awt.event.InputEvent mouseEvent, int leafX, int leafY, int transactionId)
	{
		super(transactionId);
		this.type = type;
		this.srcElement = srcElement;
		this.leafX = leafX;
		this.leafY = leafY;
		inputEvent = mouseEvent;
	}
	
	public Event(String type, HTMLElement srcElement, java.awt.event.KeyEvent keyEvent, int transactionId)
	{
		super(transactionId);
		this.type = type;
		this.srcElement = srcElement;
		inputEvent = keyEvent;
	}
	
	public boolean getAltKey()
	{
		return inputEvent.isAltDown();
	}
	
	public int getButton()
	{
		InputEvent ie = inputEvent;
		if (ie instanceof MouseEvent)
		{
			return ((MouseEvent) ie).getButton();
		}
		else
		{
			return 0;
		}
	}
	
	public int getClientX()
	{
		InputEvent ie = inputEvent;
		if (ie instanceof MouseEvent)
		{
			return ((MouseEvent) ie).getX();
		}
		else
		{
			return 0;
		}
	}
	
	public int getClientY()
	{
		InputEvent ie = inputEvent;
		if (ie instanceof MouseEvent)
		{
			return ((MouseEvent) ie).getY();
		}
		else
		{
			return 0;
		}
	}
	
	public boolean getCtrlKey()
	{
		return inputEvent.isControlDown();
	}
	
	public HTMLElement getFromElement()
	{
		return fromElement;
	}
	
	public int getKeyCode()
	{
		InputEvent ie = inputEvent;
		if (ie instanceof KeyEvent)
		{
			return ((KeyEvent) ie).getKeyCode();
		}
		else
		{
			return 0;
		}
	}
	
	public int getLeafX()
	{
		return leafX;
	}
	
	public int getLeafY()
	{
		return leafY;
	}
	
	public boolean getShiftKey()
	{
		return inputEvent.isShiftDown();
	}
	
	public HTMLElement getSrcElement()
	{
		return srcElement;
	}
	
	// public int getOffsetX() {
	// // Despite advertising that it returns an element-relative offset,
	// // IE doesn't do this.
	// //TODO: Must be relative to top viewport.
	// return this.getClientX() - 2;
	// }
	//
	// public int getOffsetY() {
	// // Despite advertising that it returns an element-relative offset,
	// // IE doesn't do this.
	// //TODO: Must be relative to top viewport.
	// return this.getClientY() - 2;
	// }
	
	public HTMLElement getToElement()
	{
		return toElement;
	}
	
	public String getType()
	{
		return type;
	}
	
	public boolean isCancelBubble()
	{
		return cancelBubble;
	}
	
	public boolean isReturnValue()
	{
		return returnValue;
	}
	
	public void setCancelBubble(boolean cancelBubble)
	{
		this.cancelBubble = cancelBubble;
	}
	
	public void setFromElement(HTMLElement fromElement)
	{
		this.fromElement = fromElement;
	}
	
	public void setLeafX(int leafX)
	{
		this.leafX = leafX;
	}
	
	public void setLeafY(int leafY)
	{
		this.leafY = leafY;
	}
	
	public void setReturnValue(boolean returnValue)
	{
		this.returnValue = returnValue;
	}
	
	public void setSrcElement(HTMLElement srcElement)
	{
		this.srcElement = srcElement;
	}
	
	public void setToElement(HTMLElement toElement)
	{
		this.toElement = toElement;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
}
