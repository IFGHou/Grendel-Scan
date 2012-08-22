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

package org.cobra_grendel.html;

/**
 * @deprecated This interface is no longer used. Note that JavaScript cookie
 *             functionality has been moved to {@link UserAgentContext}.
 */
@Deprecated
public interface HtmlParserContext
{
	/**
	 * Gets the user agent context.
	 */
	public UserAgentContext getUserAgentContext();
	
	// /**
	// * Gets a semi-colon-separated list of name=value pairs
	// * corresponding to persisted cookies available in the current
	// * document context.
	// */
	// public String getCookie();
	//	
	// /**
	// * Addes a cookie in the current document context.
	// * The specfication is equivalent to that of a Set-Cookie HTTP header.
	// */
	// public void setCookie(String cookie);
	//	
	//	
	// /**
	// * Informs context about a warning.
	// */
	// public void warn(String message, Throwable throwable);
	//
	// /**
	// * Informs context about an error.
	// */
	// public void error(String message, Throwable throwable);
	//	
	// /**
	// * Informs context about a warning.
	// */
	// public void warn(String message);
	//
	// /**
	// * Informs context about an error.
	// */
	// public void error(String message);
}
