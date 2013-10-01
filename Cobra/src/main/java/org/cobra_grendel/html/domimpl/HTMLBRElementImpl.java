package org.cobra_grendel.html.domimpl;

import org.w3c.dom.html2.HTMLBRElement;

public class HTMLBRElementImpl extends HTMLElementImpl implements HTMLBRElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLBRElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public String getClear()
	{
		return getAttribute("clear");
	}
	
	@Override public void setClear(String clear)
	{
		setAttribute("clear", clear);
	}
	
	@Override
	protected void appendInnerTextImpl(StringBuffer buffer)
	{
		buffer.append("\r\n");
		super.appendInnerTextImpl(buffer);
	}
}
