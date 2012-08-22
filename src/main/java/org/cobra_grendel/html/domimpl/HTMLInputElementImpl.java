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

import java.util.logging.Level;

import org.cobra_grendel.html.FormInput;
import org.w3c.dom.html2.HTMLInputElement;

public class HTMLInputElementImpl extends HTMLBaseInputElement implements HTMLInputElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private boolean defaultChecked;
	
	public HTMLInputElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public void click()
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.click();
		}
	}
	
	@Override public boolean getChecked()
	{
		InputContext ic = inputContext;
		return ic == null ? false : ic.getChecked();
	}
	
	@Override public boolean getDefaultChecked()
	{
		return defaultChecked;
	}
	
	@Override public int getMaxLength()
	{
		InputContext ic = inputContext;
		return ic == null ? 0 : ic.getMaxLength();
	}
	
	@Override public int getSize()
	{
		InputContext ic = inputContext;
		return ic == null ? 0 : ic.getControlSize();
	}
	
	@Override public String getSrc()
	{
		return getAttribute("src");
	}
	
	/**
	 * Gets input type in lowercase.
	 */
	@Override public String getType()
	{
		String type = getAttribute("type");
		return type == null ? null : type.toLowerCase();
	}
	
	@Override public String getUseMap()
	{
		return getAttribute("usemap");
	}
	
	public boolean isImageInput()
	{
		String type = getType();
		return "image".equals(type);
	}
	
	public boolean isResetInput()
	{
		String type = getType();
		return "reset".equals(type);
	}
	
	public boolean isSubmitInput()
	{
		String type = getType();
		return "submit".equals(type);
	}
	
	public boolean isSubmittableWithEnterKey()
	{
		String type = getType();
		return ((type == null) || "".equals(type) || "text".equals(type) || "password".equals(type));
	}
	
	public boolean isSubmittableWithPress()
	{
		String type = getType();
		return "submit".equals(type) || "image".equals(type);
	}
	
	@Override public void setChecked(boolean checked)
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.setChecked(checked);
		}
	}
	
	@Override public void setDefaultChecked(boolean defaultChecked)
	{
		this.defaultChecked = defaultChecked;
	}
	
	@Override public void setMaxLength(int maxLength)
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.setMaxLength(maxLength);
		}
	}
	
	@Override public void setSize(int size)
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.setControlSize(size);
		}
	}
	
	@Override public void setSrc(String src)
	{
		setAttribute("src", src);
	}
	
	@Override public void setType(String type)
	{
		setAttribute("type", type);
	}
	
	@Override public void setUseMap(String useMap)
	{
		setAttribute("usemap", useMap);
	}
	
	@Override
	public FormInput[] getFormInputs()
	{
		String type = getType();
		String name = getName();
		if (name == null)
		{
			return null;
		}
		if (type == null)
		{
			return new FormInput[] { new FormInput(name, getValue()) };
		}
		else
		{
			if ("text".equals(type) || "password".equals(type) || "hidden".equals(type) || "".equals(type))
			{
				return new FormInput[] { new FormInput(name, getValue()) };
			}
			else if ("submit".equals(type))
			{
				// It's done as an "extra" form input
				return null;
			}
			else if ("radio".equals(type) || "checkbox".equals(type))
			{
				if (getChecked())
				{
					String value = getValue();
					if ((value == null) || (value.length() == 0))
					{
						value = "on";
					}
					return new FormInput[] { new FormInput(name, value) };
				}
				else
				{
					return null;
				}
			}
			else if ("image".equals(type))
			{
				// It's done as an "extra" form input
				return null;
			}
			else if ("file".equals(type))
			{
				java.io.File file = getFileValue();
				if (file == null)
				{
					if (logger.isLoggable(Level.INFO))
					{
						logger.info("getFormInputs(): File input named " + name + " has null file.");
					}
					return null;
				}
				else
				{
					return new FormInput[] { new FormInput(name, file) };
				}
			}
			else
			{
				return null;
			}
		}
	}
	
	@Override
	void resetInput()
	{
		InputContext ic = inputContext;
		if (ic != null)
		{
			ic.resetInput();
		}
	}
}
