package org.cobra_grendel.html.domimpl;

import org.w3c.dom.html2.HTMLBRElement;

public class HTMLBRElementImpl extends HTMLElementImpl implements HTMLBRElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLBRElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    protected void appendInnerTextImpl(final StringBuffer buffer)
    {
        buffer.append("\r\n");
        super.appendInnerTextImpl(buffer);
    }

    @Override
    public String getClear()
    {
        return getAttribute("clear");
    }

    @Override
    public void setClear(final String clear)
    {
        setAttribute("clear", clear);
    }
}
