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

package org.cobra_grendel.html.js;

import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.js.AbstractScriptableDelegate;

public class Navigator extends AbstractScriptableDelegate
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public class MimeTypesCollection
	{
		// Class must be public to allow JavaScript access
		public int getLength()
		{
			return 0;
		}
		
		public Object item(int index)
		{
			return null;
		}
		
		public Object namedItem(String name)
		{
			return null;
		}
	}
	
	private final UserAgentContext context;
	
	private MimeTypesCollection mimeTypes;
	
	/**
	 * @param context
	 */
	public Navigator(UserAgentContext context)
	{
		super(-1);
		this.context = context;
	}
	
	public String getAppCodeName()
	{
		return context.getAppCodeName();
	}
	
	public String getAppMinorVersion()
	{
		return context.getAppMinorVersion();
	}
	
	public String getAppName()
	{
		return context.getAppName();
	}
	
	public String getAppVersion()
	{
		return context.getAppVersion();
	}
	
	public MimeTypesCollection getMimeTypes()
	{
		synchronized (this)
		{
			MimeTypesCollection mt = mimeTypes;
			if (mt == null)
			{
				mt = new MimeTypesCollection();
				mimeTypes = mt;
			}
			return mt;
		}
	}
	
	public String getPlatform()
	{
		return context.getPlatform();
	}
	
	public String getUserAgent()
	{
		return context.getUserAgent();
	}
	
	public boolean javaEnabled()
	{
		// True always?
		return true;
	}
}
