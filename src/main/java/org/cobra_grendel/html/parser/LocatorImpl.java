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
 * Created on Oct 16, 2005
 */
package org.cobra_grendel.html.parser;

import org.xml.sax.Locator;

class LocatorImpl implements Locator
{
	private final int columnNumber;
	private final int lineNumber;
	private final String publicId;
	private final String systemId;
	
	/**
	 * @param pid
	 * @param sid
	 * @param lnumber
	 * @param cnumber
	 */
	public LocatorImpl(String pid, String sid, int lnumber, int cnumber)
	{
		super();
		publicId = pid;
		systemId = sid;
		lineNumber = lnumber;
		columnNumber = cnumber;
	}
	
	@Override public int getColumnNumber()
	{
		return columnNumber;
	}
	
	@Override public int getLineNumber()
	{
		return lineNumber;
	}
	
	@Override public String getPublicId()
	{
		return publicId;
	}
	
	@Override public String getSystemId()
	{
		return systemId;
	}
}
