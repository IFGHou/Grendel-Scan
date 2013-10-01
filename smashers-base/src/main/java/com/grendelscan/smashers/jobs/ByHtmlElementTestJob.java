package com.grendelscan.smashers.jobs;

import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByHtmlElementTest;

public class ByHtmlElementTestJob extends TransactionTestJob
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final HTMLElement element;
    private final String elementType;

    public ByHtmlElementTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final HTMLElement element, final String elementType)
    {
        super(moduleClass, transactionID);
        this.element = element;
        this.elementType = elementType;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHtmlElementTest) getModule()).testByHtmlElement(transactionID, element, elementType, getId());
    }
}
