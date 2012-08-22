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
 * Created on Nov 12, 2005
 */
package org.cobra_grendel.html.js;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.cobra_grendel.html.HtmlRendererContext;
import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.domimpl.HTMLDocumentImpl;
import org.cobra_grendel.html.domimpl.HTMLIFrameElementImpl;
import org.cobra_grendel.html.domimpl.HTMLImageElementImpl;
import org.cobra_grendel.html.domimpl.HTMLOptionElementImpl;
import org.cobra_grendel.html.domimpl.HTMLScriptElementImpl;
import org.cobra_grendel.html.domimpl.HTMLSelectElementImpl;
import org.cobra_grendel.js.AbstractScriptableDelegate;
import org.cobra_grendel.js.JavaClassWrapper;
import org.cobra_grendel.js.JavaClassWrapperFactory;
import org.cobra_grendel.js.JavaInstantiator;
import org.cobra_grendel.js.JavaObjectWrapper;
import org.cobra_grendel.js.JavaScript;
import org.cobra_grendel.util.ID;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLCollection;

public class Window extends AbstractScriptableDelegate
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static final Map CONTEXT_WINDOWS = new WeakHashMap();
	private static final Logger logger = Logger.getLogger(Window.class.getName());
	// private static final JavaClassWrapper IMAGE_WRAPPER =
	// JavaClassWrapperFactory.getInstance().getClassWrapper(Image.class);
	private static final JavaClassWrapper XMLHTTPREQUEST_WRAPPER =
	        JavaClassWrapperFactory.getInstance().getClassWrapper(XMLHttpRequest.class);
	
	private volatile HTMLDocumentImpl document;
	private Location location;
	
	private Navigator navigator;
	private Function onunload;
	private final HtmlRendererContext rcontext;
	private Screen screen;
	private Map taskMap;
	
	private final UserAgentContext uaContext;
	private ScriptableObject windowScope;
	
	public Window(HtmlRendererContext rcontext, UserAgentContext uaContext)
	{
		super(-1);
		// TODO: Probably need to create a new Window instance
		// for every document. Sharing of Window state between
		// different documents is not correct.
		this.rcontext = rcontext;
		this.uaContext = uaContext;
	}
	
	public static Window getWindow(HtmlRendererContext rcontext)
	{
		if (rcontext == null)
		{
			return null;
		}
		synchronized (CONTEXT_WINDOWS)
		{
			Reference wref = (Reference) CONTEXT_WINDOWS.get(rcontext);
			if (wref != null)
			{
				Window window = (Window) wref.get();
				if (window != null)
				{
					return window;
				}
			}
			Window window = new Window(rcontext, rcontext.getUserAgentContext());
			CONTEXT_WINDOWS.put(rcontext, new WeakReference(window));
			return window;
		}
	}
	
	/**
	 * Disabled to prevent problems in scanner
	 */
	public void alert(String message)
	{
		System.out.println("ALERT: " + message);
		/*
		 * if(this.rcontext != null) { this.rcontext.alert(message); }
		 */
	}
	
	/**
	 * Disabled to prevent problems in scanner
	 */
	public void back()
	{
		/*
		 * HtmlRendererContext rcontext = this.rcontext; if(rcontext != null) {
		 * rcontext.back(); }
		 */}
	
	public void blur()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			rcontext.blur();
		}
	}
	
	// private Timer getTask(Long timeoutID) {
	// synchronized(this) {
	// Map taskMap = this.taskMap;
	// if(taskMap != null) {
	// return (Timer) taskMap.get(timeoutID);
	// }
	// }
	// return null;
	// }
	
	public void clearTimeout(int timeoutID)
	{
		Integer key = new Integer(timeoutID);
		forgetTask(key, true);
	}
	
	public void close()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			rcontext.close();
		}
	}
	
	/**
	 * Disabled to prevent problems in scanner
	 * 
	 * @param message
	 * @return Always returns true
	 */
	public boolean confirm(String message)
	{
		/*
		 * HtmlRendererContext rcontext = this.rcontext; if(rcontext != null) {
		 * return rcontext.confirm(message); } else { return false; }
		 */
		return true;
	}
	
	public Object eval(String javascript)
	{
		HTMLDocumentImpl document = this.document;
		if (document == null)
		{
			throw new IllegalStateException("Cannot evaluate if document is not set.");
		}
		Context ctx = Executor.createContext(document.getDocumentURL(), uaContext);
		try
		{
			Scriptable scope = getWindowScope();
			if (scope == null)
			{
				throw new IllegalStateException(
				        "Scriptable (scope) instance was expected to be keyed as UserData to document using "
				                + Executor.SCOPE_KEY);
			}
			String scriptURI = "window.eval";
			if (logger.isLoggable(Level.INFO))
			{
				logger.info("eval(): javascript follows...\r\n" + javascript);
			}
			return ctx.evaluateString(scope, javascript, scriptURI, 1, null);
		}
		finally
		{
			Context.exit();
		}
	}
	
	public void focus()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			rcontext.focus();
		}
	}
	
	public String getDefaultStatus()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return rcontext.getDefaultStatus();
		}
		else
		{
			return null;
		}
	}
	
	public Document getDocument()
	{
		return document;
	}
	
	public HTMLCollection getFrames()
	{
		Document doc = document;
		if (doc instanceof HTMLDocumentImpl)
		{
			return ((HTMLDocumentImpl) doc).getFrames();
		}
		return null;
	}
	
	public HtmlRendererContext getHtmlRendererContext()
	{
		return rcontext;
	}
	
	/**
	 * Gets the number of frames.
	 */
	public int getLength()
	{
		HTMLCollection frames = getFrames();
		return frames == null ? 0 : frames.getLength();
	}
	
	public Location getLocation()
	{
		synchronized (this)
		{
			Location location = this.location;
			if (location == null)
			{
				location = new Location(this, -1);
				this.location = location;
			}
			return location;
		}
	}
	
	public String getName()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return rcontext.getName();
		}
		else
		{
			return null;
		}
	}
	
	public Navigator getNavigator()
	{
		synchronized (this)
		{
			Navigator nav = navigator;
			if (nav == null)
			{
				nav = new Navigator(uaContext);
				navigator = nav;
			}
			return nav;
		}
	}
	
	public Function getOnload()
	{
		Document doc = document;
		if (doc instanceof HTMLDocumentImpl)
		{
			return ((HTMLDocumentImpl) doc).getOnloadHandler();
		}
		else
		{
			return null;
		}
	}
	
	public Function getOnunload()
	{
		return onunload;
	}
	
	public Window getOpener()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return Window.getWindow(rcontext.getOpener());
		}
		else
		{
			return null;
		}
	}
	
	public Window getParent()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return Window.getWindow(rcontext.getParent());
		}
		else
		{
			return null;
		}
	}
	
	public Screen getScreen()
	{
		synchronized (this)
		{
			Screen nav = screen;
			if (nav == null)
			{
				nav = new Screen();
				screen = nav;
			}
			return nav;
		}
	}
	
	public Window getSelf()
	{
		return this;
	}
	
	public String getStatus()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return rcontext.getStatus();
		}
		else
		{
			return null;
		}
	}
	
	public Window getTop()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return Window.getWindow(rcontext.getTop());
		}
		else
		{
			return null;
		}
	}
	
	public Window getWindow()
	{
		return this;
	}
	
	public Scriptable getWindowScope()
	{
		synchronized (this)
		{
			ScriptableObject windowScope = this.windowScope;
			if (windowScope != null)
			{
				return windowScope;
			}
			// Context.enter() OK in this particular case.
			Context ctx = Context.enter();
			try
			{
				// Window scope needs to be top-most scope.
				windowScope = (ScriptableObject) JavaScript.getInstance().getJavascriptObject(this, null);
				ctx.initStandardObjects(windowScope);
				
				// Special Javascript class: XMLHttpRequest
				final Scriptable ws = windowScope;
				JavaInstantiator xi = new JavaInstantiator()
				{
					/**
					 * 
					 */
					private static final long	serialVersionUID	= 1L;

					@Override public Object newInstance()
					{
						HTMLDocumentImpl doc = document;
						if (doc == null)
						{
							throw new IllegalStateException("Cannot perform operation when document is unset.");
						}
						return new XMLHttpRequest(uaContext, doc.getDocumentURL(), ws, doc.getTransactionId());
					}
				};
				Function xmlHttpRequestC =
				        JavaObjectWrapper.getConstructor("XMLHttpRequest", XMLHTTPREQUEST_WRAPPER, windowScope, xi);
				ScriptableObject.defineProperty(windowScope, "XMLHttpRequest", xmlHttpRequestC,
				        ScriptableObject.READONLY);
				
				// HTML element classes
				defineElementClass(windowScope, "Image", "img", HTMLImageElementImpl.class);
				defineElementClass(windowScope, "Script", "script", HTMLScriptElementImpl.class);
				defineElementClass(windowScope, "IFrame", "iframe", HTMLIFrameElementImpl.class);
				defineElementClass(windowScope, "Option", "option", HTMLOptionElementImpl.class);
				defineElementClass(windowScope, "Select", "select", HTMLSelectElementImpl.class);
				
				this.windowScope = windowScope;
				return windowScope;
			}
			finally
			{
				Context.exit();
			}
		}
	}
	
	public boolean isClosed()
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			return rcontext.isClosed();
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * For now, disabled to prevent problems with scanner. May be reenabled if
	 * possible.
	 * 
	 * @param url
	 * @param windowName
	 * @return Always returns null
	 */
	public Window open(String url, String windowName)
	{
		// return this.open(url, windowName, "", false);
		return null;
	}
	
	/**
	 * For now, disabled to prevent problems with scanner. May be reenabled if
	 * possible.
	 * 
	 * @param url
	 * @param windowName
	 * @param windowFeatures
	 * @return Always returns null
	 */
	public Window open(String url, String windowName, String windowFeatures)
	{
		// return this.open(url, windowName, windowFeatures, false);
		return null;
	}
	
	/**
	 * For now, disabled to prevent problems with scanner. May be reenabled if
	 * possible.
	 * 
	 * @param relativeUrl
	 * @param windowName
	 * @param windowFeatures
	 * @param replace
	 * @return Always returns null
	 */
	public Window open(String relativeUrl, String windowName, String windowFeatures, boolean replace)
	{
		/*
		 * HtmlRendererContext rcontext = this.rcontext; if(rcontext != null) {
		 * java.net.URL url; Object document = this.document; if(document
		 * instanceof HTMLDocumentImpl) { url = ((HTMLDocumentImpl)
		 * document).getFullURL(relativeUrl); } else { try { url = new
		 * java.net.URL(relativeUrl); } catch(java.net.MalformedURLException
		 * mfu) { throw new IllegalArgumentException("Malformed URI: " +
		 * relativeUrl); } } HtmlRendererContext newContext = rcontext.open(url,
		 * windowName, windowFeatures, replace); return getWindow(newContext); }
		 * else { return null; }
		 */
		return null;
	}
	
	/**
	 * Disabled to prevent problems with the scanner.
	 * 
	 * @param message
	 * @return Always returns an empty string
	 */
	public String prompt(String message)
	{
		return "";
		// return this.prompt(message, "");
	}
	
	/**
	 * Disabled to prevent problems with the scanner.
	 * 
	 * @param message
	 * @param inputDefault
	 * @return Always returns the default
	 */
	public String prompt(String message, int inputDefault)
	{
		// return this.prompt(message, String.valueOf(inputDefault));
		return String.valueOf(inputDefault);
	}
	
	/**
	 * Disabled to prevent problems with the scanner.
	 * 
	 * @param message
	 * @param inputDefault
	 * @returnAlways returns the default
	 */
	public String prompt(String message, String inputDefault)
	{
		return inputDefault;
		/*
		 * HtmlRendererContext rcontext = this.rcontext; if(rcontext != null) {
		 * return rcontext.prompt(message, inputDefault); } else { return null; }
		 */
	}
	
	public void scroll(int x, int y)
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			rcontext.scroll(x, y);
		}
	}
	
	public void setDocument(HTMLDocumentImpl document)
	{
		Document prevDocument = this.document;
		if (prevDocument != document)
		{
			forgetAllTasks();
			Function onunload = this.onunload;
			if (onunload != null)
			{
				HTMLDocumentImpl oldDoc = this.document;
				Executor.executeFunction(getWindowScope(), onunload, oldDoc.getDocumentURL(), uaContext);
				this.onunload = null;
			}
			this.document = document;
		}
	}
	
	/**
	 * Not in Cobra, but easy to implement. The interval is always zero, and it
	 * only gets execute once.
	 * 
	 * @param function
	 * @param millis
	 * @return
	 */
	public int setInterval(Function function, double millis)
	{
		return setTimeout(function, millis);
	}
	
	/**
	 * Not in Cobra, but easy to implement. The interval is always zero, and it
	 * only gets execute once.
	 * 
	 * @param expr
	 * @param millis
	 * @return
	 */
	public int setInterval(String expr, double millis)
	{
		return setTimeout(expr, millis);
	}
	
	/**
	 * Disabled to prevent problems with scanner. Will be reenabled in the
	 * future.
	 * 
	 * @param location
	 */
	public void setLocation(String location)
	{
		// this.getLocation().setHref(location);
	}
	
	public void setOnload(Function onload)
	{
		// Note that body.onload overrides
		// window.onload.
		Document doc = document;
		if (doc instanceof HTMLDocumentImpl)
		{
			((HTMLDocumentImpl) doc).setOnloadHandler(onload);
		}
	}
	
	public void setOnunload(Function onunload)
	{
		this.onunload = onunload;
	}
	
	public void setOpener(Window opener)
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			if (opener == null)
			{
				rcontext.setOpener(null);
			}
			else
			{
				rcontext.setOpener(opener.rcontext);
			}
		}
	}
	
	public void setStatus(String message)
	{
		HtmlRendererContext rcontext = this.rcontext;
		if (rcontext != null)
		{
			rcontext.setStatus(message);
		}
	}
	
	/**
	 * Delay is always set to zero to prevent problems in the scanner
	 * 
	 * @param function
	 * @param millis
	 * @return
	 */
	public int setTimeout(final Function function, double millis)
	{
		millis = 0;
		final int timeID = ID.generateInt();
		final Integer timeIDInt = new Integer(timeID);
		ActionListener task = new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				// This executes in the GUI thread and that's good.
				Window.this.forgetTask(timeIDInt, false);
				HTMLDocumentImpl doc = document;
				if (doc == null)
				{
					throw new IllegalStateException("Cannot perform operation when document is unset.");
				}
				Executor.executeFunction(getWindowScope(), function, doc.getDocumentURL(), uaContext);
			}
		};
		if ((millis > Integer.MAX_VALUE) || (millis < 0))
		{
			throw new IllegalArgumentException("Timeout value " + millis + " is not supported.");
		}
		Timer timer = new Timer((int) millis, task);
		timer.setRepeats(false);
		timer.start();
		putTask(timeIDInt, timer);
		return timeID;
	}
	
	/**
	 * Delay is always set to zero to prevent problems in the scanner
	 * 
	 * @param expr
	 * @param millis
	 * @return
	 */
	public int setTimeout(final String expr, double millis)
	{
		millis = 0;
		final int timeID = ID.generateInt();
		final Integer timeIDInt = new Integer(timeID);
		ActionListener task = new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				// This executes in the GUI thread and that's good.
				Window.this.forgetTask(timeIDInt, false);
				Window.this.eval(expr);
			}
		};
		if ((millis > Integer.MAX_VALUE) || (millis < 0))
		{
			throw new IllegalArgumentException("Timeout value " + millis + " is not supported.");
		}
		Timer timer = new Timer((int) millis, task);
		timer.setRepeats(false);
		timer.start();
		putTask(timeIDInt, timer);
		return timeID;
	}
	
	/**
	 * Used for testing for XSS using a function name that won't be blocked by
	 * stupid filters
	 * 
	 * @param token
	 */
	public void testXSS(String token)
	{
		document.setXssToken(token);
	}
	
	private final void defineElementClass(Scriptable scope, final String jsClassName, final String elementName,
	        Class javaClass)
	{
		JavaInstantiator ji = new JavaInstantiator()
		{
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override public Object newInstance()
			{
				Document document = Window.this.document;
				if (document == null)
				{
					throw new IllegalStateException("Document not set in current context.");
				}
				return document.createElement(elementName);
			}
		};
		JavaClassWrapper classWrapper = JavaClassWrapperFactory.getInstance().getClassWrapper(javaClass);
		Function constructorFunction = JavaObjectWrapper.getConstructor(jsClassName, classWrapper, scope, ji);
		ScriptableObject.defineProperty(scope, jsClassName, constructorFunction, ScriptableObject.READONLY);
	}
	
	private void forgetAllTasks()
	{
		synchronized (this)
		{
			Map taskMap = this.taskMap;
			if (taskMap != null)
			{
				Iterator i = taskMap.values().iterator();
				while (i.hasNext())
				{
					Timer timer = (Timer) i.next();
					timer.stop();
				}
				this.taskMap = null;
			}
		}
	}
	
	private void forgetTask(Integer timeoutID, boolean cancel)
	{
		synchronized (this)
		{
			Map taskMap = this.taskMap;
			if (taskMap != null)
			{
				Timer timer = (Timer) taskMap.remove(timeoutID);
				if ((timer != null) && cancel)
				{
					timer.stop();
				}
			}
		}
	}
	
	private void putTask(Integer timeoutID, Timer timer)
	{
		synchronized (this)
		{
			Map taskMap = this.taskMap;
			if (taskMap == null)
			{
				taskMap = new HashMap();
				this.taskMap = taskMap;
			}
			taskMap.put(timeoutID, timer);
		}
	}
	
}
