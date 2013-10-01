package org.cobra_grendel.html.domimpl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLOptionsCollection;

public class HTMLOptionsCollectionImpl extends DescendentHTMLCollection implements HTMLOptionsCollection
{
    private static class OptionFilter implements NodeFilter
    {
        @Override
        public boolean accept(final Node node)
        {
            return "option".equalsIgnoreCase(node.getNodeName());
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static final NodeFilter OPTION_FILTER = new OptionFilter();

    public HTMLOptionsCollectionImpl(final HTMLElementImpl selectElement, final int transactionId)
    {
        super(selectElement, OPTION_FILTER, transactionId);
    }

    @Override
    public void setLength(final int length) throws DOMException
    {
        // TODO: ???
        throw new UnsupportedOperationException();
    }

}
