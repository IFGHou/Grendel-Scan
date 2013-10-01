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

package org.cobra_grendel.html.test;

import org.cobra_grendel.html.HtmlParserContext;
import org.cobra_grendel.html.UserAgentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>SimpleHtmlParserContext</code> is a simple implementation of the {@link org.cobra_grendel.html.HtmlParserContext} interface. Methods in this class should be overridden to provide
 * functionality such as cookies.
 * 
 * @author J. H. S.
 */
public class SimpleHtmlParserContext implements HtmlParserContext
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHtmlParserContext.class);

    private UserAgentContext bcontext = null;

    public SimpleHtmlParserContext()
    {
        super();
    }

    public void error(final String message)
    {
        LOGGER.error(message);
    }

    public void error(final String message, final Throwable throwable)
    {
        LOGGER.error(message, throwable);
    }

    public String getCookie()
    {
        return "";
    }

    @Override
    public UserAgentContext getUserAgentContext()
    {
        this.warn("getUserAgentContext(): Not overridden; returning simple one.");
        synchronized (this)
        {
            if (bcontext == null)
            {
                bcontext = new SimpleUserAgentContext();
            }
            return bcontext;
        }
    }

    public void setCookie(final String cookie)
    {
        this.warn("setCookie(): Not overridden");
    }

    public void warn(final String message)
    {
        LOGGER.warn(message);
    }

    public void warn(final String message, final Throwable throwable)
    {
        LOGGER.warn(message, throwable);
    }
}
