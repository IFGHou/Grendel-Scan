package com.grendelscan.tests.testJobs;

import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHtmlElementTest;

public class ByHtmlElementTestJob extends TransactionTestJob
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private HTMLElement			element;
	private String				elementType;

	public ByHtmlElementTestJob(Class<? extends TestModule> moduleClass, int transactionID, HTMLElement element, String elementType)
	{
		super(moduleClass, transactionID);
		this.element = element;
		this.elementType = elementType;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByHtmlElementTest) getModule()).testByHtmlElement(transactionID, element, elementType, this.getId());
	}
}
