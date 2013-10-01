package org.cobra_grendel.html.domimpl;

public class HTMLNonStandardElement extends HTMLElementImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLNonStandardElement(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	public HTMLNonStandardElement(String name, boolean noStyleSheet, int transactionId)
	{
		super(name, noStyleSheet, transactionId);
	}
}
