package org.cobra_grendel.html.domimpl;

import java.util.HashMap;
import java.util.Map;

import org.cobra_grendel.html.UserAgentContext;
import org.cobra_grendel.html.js.Executor;
import org.cobra_grendel.js.JavaScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;

/**
 * Implements common functionality of most elements.
 */
public class HTMLAbstractUIElement extends HTMLElementImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private Map functionByAttribute = null;
	
	private Function onfocus, onblur, onclick, ondblclick, onmousedown, onmouseup, onmouseover, onmousemove,
	        onmouseout, onkeypress, onkeydown, onkeyup;
	
	public HTMLAbstractUIElement(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	public void blur()
	{
		UINode node = getUINode();
		if (node != null)
		{
			node.blur();
		}
	}
	
	public void focus()
	{
		UINode node = getUINode();
		if (node != null)
		{
			node.focus();
		}
	}
	
	public Function getOnblur()
	{
		return getEventFunction(onblur, "onblur");
	}
	
	public Function getOnclick()
	{
		return getEventFunction(onclick, "onclick");
	}
	
	public Function getOndblclick()
	{
		return getEventFunction(ondblclick, "ondblclick");
	}
	
	public Function getOnfocus()
	{
		return getEventFunction(onfocus, "onfocus");
	}
	
	public Function getOnkeydown()
	{
		return getEventFunction(onkeydown, "onkeydown");
	}
	
	public Function getOnkeypress()
	{
		return getEventFunction(onkeypress, "onkeypress");
	}
	
	public Function getOnkeyup()
	{
		return getEventFunction(onkeyup, "onkeyup");
	}
	
	public Function getOnmousedown()
	{
		return getEventFunction(onmousedown, "onmousedown");
	}
	
	public Function getOnmousemove()
	{
		return getEventFunction(onmousemove, "onmousemove");
	}
	
	public Function getOnmouseout()
	{
		return getEventFunction(onmouseout, "onmouseout");
	}
	
	public Function getOnmouseover()
	{
		return getEventFunction(onmouseover, "onmouseover");
	}
	
	public Function getOnmouseup()
	{
		return getEventFunction(onmouseup, "onmouseup");
	}
	
	public void setOnblur(Function onblur)
	{
		this.onblur = onblur;
	}
	
	public void setOnclick(Function onclick)
	{
		this.onclick = onclick;
	}
	
	public void setOndblclick(Function ondblclick)
	{
		this.ondblclick = ondblclick;
	}
	
	public void setOnfocus(Function onfocus)
	{
		this.onfocus = onfocus;
	}
	
	public void setOnkeydown(Function onkeydown)
	{
		this.onkeydown = onkeydown;
	}
	
	public void setOnkeypress(Function onkeypress)
	{
		this.onkeypress = onkeypress;
	}
	
	public void setOnkeyup(Function onkeyup)
	{
		this.onkeyup = onkeyup;
	}
	
	public void setOnmousedown(Function onmousedown)
	{
		this.onmousedown = onmousedown;
	}
	
	public void setOnmousemove(Function onmousemove)
	{
		this.onmousemove = onmousemove;
	}
	
	public void setOnmouseout(Function onmouseout)
	{
		this.onmouseout = onmouseout;
	}
	
	public void setOnmouseover(Function onmouseover)
	{
		this.onmouseover = onmouseover;
	}
	
	public void setOnmouseup(Function onmouseup)
	{
		this.onmouseup = onmouseup;
	}
	
	@Override
	protected void assignAttributeField(String normalName, String value)
	{
		super.assignAttributeField(normalName, value);
		if (normalName.startsWith("on"))
		{
			synchronized (this)
			{
				Map fba = functionByAttribute;
				if (fba != null)
				{
					fba.remove(normalName);
				}
			}
		}
	}
	
	protected Function getEventFunction(Function varValue, String attributeName)
	{
		if (varValue != null)
		{
			return varValue;
		}
		String normalAttributeName = normalizeAttributeName(attributeName);
		synchronized (this)
		{
			Map fba = functionByAttribute;
			Function f = fba == null ? null : (Function) fba.get(normalAttributeName);
			if (f != null)
			{
				return f;
			}
			UserAgentContext uac = getUserAgentContext();
			if (uac == null)
			{
				throw new IllegalStateException("No user agent context.");
			}
			if (uac.isScriptingEnabled())
			{
				String attributeValue = getAttribute(attributeName);
				if ((attributeValue == null) || (attributeValue.length() == 0))
				{
					f = null;
				}
				else
				{
					String functionCode =
					        "function " + normalAttributeName + "_" + System.identityHashCode(this) + "() { "
					                + attributeValue + " }";
					Document doc = document;
					if (doc == null)
					{
						throw new IllegalStateException("Element does not belong to a document.");
					}
					Context ctx = Executor.createContext(getDocumentURL(), uac);
					try
					{
						Scriptable scope = (Scriptable) doc.getUserData(Executor.SCOPE_KEY);
						if (scope == null)
						{
							throw new IllegalStateException(
							        "Scriptable (scope) instance was expected to be keyed as UserData to document using "
							                + Executor.SCOPE_KEY);
						}
						Scriptable thisScope = (Scriptable) JavaScript.getInstance().getJavascriptObject(this, scope);
						try
						{
							// TODO: Get right line number for script. //TODO:
							// Optimize this in case it's called multiple times?
							// Is that done?
							f =
							        ctx.compileFunction(thisScope, functionCode, getTagName() + "[" + getId() + "]."
							                + attributeName, 1, null);
						}
						catch (EcmaError ecmaError)
						{
							this.warn("Javascript error at " + ecmaError.getSourceName() + ":"
							        + ecmaError.getLineNumber() + ": " + ecmaError.getMessage());
							f = null;
						}
						catch (Throwable err)
						{
							this.warn("Unable to evaluate Javascript code", err);
							f = null;
						}
					}
					finally
					{
						Context.exit();
					}
				}
				if (fba == null)
				{
					fba = new HashMap(1);
					functionByAttribute = fba;
				}
				fba.put(normalAttributeName, f);
			}
			return f;
		}
	}
}
