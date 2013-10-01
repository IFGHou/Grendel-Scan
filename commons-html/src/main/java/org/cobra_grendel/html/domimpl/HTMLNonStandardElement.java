package org.cobra_grendel.html.domimpl;

public class HTMLNonStandardElement extends HTMLElementImpl
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLNonStandardElement(final String name, final boolean noStyleSheet, final int transactionId)
    {
        super(name, noStyleSheet, transactionId);
    }

    public HTMLNonStandardElement(final String name, final int transactionId)
    {
        super(name, transactionId);
    }
}
