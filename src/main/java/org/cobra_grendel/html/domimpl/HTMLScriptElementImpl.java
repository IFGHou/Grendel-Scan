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
 * Created on Oct 8, 2005
 */
package org.cobra_grendel.html.domimpl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cobra_grendel.html.HttpRequest;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.js.Executor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html2.HTMLScriptElement;

public class HTMLScriptElementImpl extends HTMLElementImpl implements HTMLScriptElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static final Logger logger = Logger.getLogger(HTMLScriptElementImpl.class.getName());
	private static final boolean loggableInfo = logger.isLoggable(Level.INFO);
	private boolean defer;
	
	private String text;
	
	public HTMLScriptElementImpl(int transactionId)
	{
		super("SCRIPT", true, transactionId);
	}
	
	public HTMLScriptElementImpl(String name, int transactionId)
	{
		super(name, true, transactionId);
	}
	
	@Override public boolean getDefer()
	{
		return defer;
	}
	
	@Override public String getEvent()
	{
		return getAttribute("event");
	}
	
	@Override public String getHtmlFor()
	{
		return getAttribute("htmlFor");
	}
	
	@Override public String getSrc()
	{
		return getAttribute("src");
	}
	
	@Override public String getText()
	{
		String t = text;
		if (t == null)
		{
			return getRawInnerText(true);
		}
		else
		{
			return t;
		}
	}
	
	@Override public String getType()
	{
		return getAttribute("type");
	}
	
	@Override public void setDefer(boolean defer)
	{
		this.defer = defer;
	}
	
	@Override public void setEvent(String event)
	{
		setAttribute("event", event);
	}
	
	@Override public void setHtmlFor(String htmlFor)
	{
		setAttribute("htmlFor", htmlFor);
	}
	
	@Override public void setSrc(String src)
	{
		setAttribute("src", src);
	}
	
	@Override public void setText(String text)
	{
		this.text = text;
	}
	
	@Override public void setType(String type)
	{
		setAttribute("type", type);
	}
	
	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler)
	{
		if (org.cobra_grendel.html.parser.HtmlParser.MODIFYING_KEY.equals(key) && (data != Boolean.TRUE))
		{
			processScript();
		}
		return super.setUserData(key, data, handler);
	}
	
	protected final void processScript()
	{
		boolean runScript = true;
		UserAgentContext bcontext = getUserAgentContext();
		if (bcontext == null)
		{
			throw new IllegalStateException("No user agent context.");
		}
		if (bcontext.isScriptingEnabled())
		{
			String text = "";
			final String scriptURI;
			int baseLineNumber = 0;
			String src = getSrc();
			Document doc = document;
			if (!(doc instanceof HTMLDocumentImpl))
			{
				throw new IllegalStateException("no valid document");
			}
			boolean liflag = loggableInfo;
			if (src == null)
			{
				text = getText();
				scriptURI = doc.getBaseURI();
				baseLineNumber = 1; // TODO: Line number of inner text??
			}
			else
			{
				
				java.net.URL scriptURL = ((HTMLDocumentImpl) doc).getFullURL(src);
				scriptURI = scriptURL == null ? src : scriptURL.toExternalForm();
				
				Pattern p = Pattern.compile("http://notreal.fake/(.+)");
				Matcher m = p.matcher(scriptURI);
				if (m.matches())
				{
					runScript = false;
					((HTMLDocumentImpl) document).setXssToken(m.group(1));
				}
				else
				{
					informExternalScriptLoading();
					long time1 = liflag ? System.currentTimeMillis() : 0;
					try
					{
						final HttpRequest request = bcontext.createHttpRequest(transactionId);
						// Perform a synchronous request
						SecurityManager sm = System.getSecurityManager();
						if (sm == null)
						{
							request.open("GET", scriptURI, false);
						}
						else
						{
							AccessController.doPrivileged(new PrivilegedAction()
							{
								@Override public Object run()
								{
									// Code might have restrictions on accessing
									// items from elsewhere.
									request.open("GET", scriptURI, false);
									return null;
								}
							});
						}
						int status = request.getStatus();
						if ((status != 200) && (status != 0))
						{
							this.warn("Script at [" + scriptURI + "] failed to load; HTTP status: " + status + ".");
							return;
						}
						text = request.getResponseText();
					}
					finally
					{
						if (liflag)
						{
							long time2 = System.currentTimeMillis();
							logger.info("processScript(): Loaded external Javascript from URI=[" + scriptURI + "] in "
							        + (time2 - time1) + " ms.");
						}
					}
					baseLineNumber = 1;
				}
			}
			if (runScript)
			{
				Context ctx = Executor.createContext(getDocumentURL(), bcontext);
				try
				{
					Scriptable scope = (Scriptable) doc.getUserData(Executor.SCOPE_KEY);
					if (scope == null)
					{
						throw new IllegalStateException(
						        "Scriptable (scope) instance was expected to be keyed as UserData to document using "
						                + Executor.SCOPE_KEY);
					}
					try
					{
						long time1 = liflag ? System.currentTimeMillis() : 0;
						ctx.evaluateString(scope, text, scriptURI, baseLineNumber, null);
						if (liflag)
						{
							long time2 = System.currentTimeMillis();
							logger.info("addNotify(): Evaluated (or attempted to evaluate) Javascript in "
							        + (time2 - time1) + " ms.");
						}
					}
					catch (EcmaError ecmaError)
					{
						this.warn("Javascript error at " + ecmaError.getSourceName() + ":" + ecmaError.getLineNumber()
						        + ": " + ecmaError.getMessage());
					}
					catch (Throwable err)
					{
						this.warn("Unable to evaluate Javascript code", err);
					}
				}
				finally
				{
					Context.exit();
				}
			}
		}
	}
}
