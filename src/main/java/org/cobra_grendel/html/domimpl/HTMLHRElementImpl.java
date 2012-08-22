package org.cobra_grendel.html.domimpl;

import org.w3c.dom.html2.HTMLHRElement;

public class HTMLHRElementImpl extends HTMLAbstractUIElement implements HTMLHRElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLHRElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public String getAlign()
	{
		return getAttribute("align");
	}
	
	@Override public boolean getNoShade()
	{
		return "noshade".equalsIgnoreCase(getAttribute("noshade"));
	}
	
	@Override public String getSize()
	{
		return getAttribute("size");
	}
	
	@Override public String getWidth()
	{
		return getAttribute("width");
	}
	
	@Override public void setAlign(String align)
	{
		setAttribute("align", align);
	}
	
	@Override public void setNoShade(boolean noShade)
	{
		setAttribute("noshade", noShade ? "noshade" : null);
	}
	
	@Override public void setSize(String size)
	{
		setAttribute("size", size);
	}
	
	@Override public void setWidth(String width)
	{
		setAttribute("width", width);
	}
}
