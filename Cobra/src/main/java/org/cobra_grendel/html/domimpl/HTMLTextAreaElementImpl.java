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
 * Created on Jan 15, 2006
 */
package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.FormInput;
import org.w3c.dom.html2.HTMLTextAreaElement;

public class HTMLTextAreaElementImpl extends HTMLBaseInputElement implements HTMLTextAreaElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLTextAreaElementImpl(int transactionId)
	{
		super("TEXTAREA", transactionId);
	}
	
	public HTMLTextAreaElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html2.HTMLTextAreaElement#getCols()
	 */
	@Override public int getCols()
	{
		InputContext ic = inputContext;
		return ic == null ? 0 : ic.getCols();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html2.HTMLTextAreaElement#getRows()
	 */
	@Override public int getRows()
	{
		InputContext ic = inputContext;
		return ic == null ? 0 : ic.getRows();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html2.HTMLTextAreaElement#getType()
	 */
	@Override public String getType()
	{
		return "textarea";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html2.HTMLTextAreaElement#setCols(int)
	 */
	@Override public void setCols(int cols)
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.setCols(cols);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html2.HTMLTextAreaElement#setRows(int)
	 */
	@Override public void setRows(int rows)
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.setRows(rows);
		}
	}
	
	@Override
	public FormInput[] getFormInputs()
	{
		String name = getName();
		if (name == null)
		{
			return null;
		}
		return new FormInput[] { new FormInput(name, getValue()) };
	}
}
