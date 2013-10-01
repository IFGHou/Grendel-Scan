package com.grendelscan.smashers.types;

import org.w3c.dom.html2.HTMLElement;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByHtmlElementTest extends TestType
{
	public String[] getHtmlElements();

	public void testByHtmlElement(int transactionID, HTMLElement element, String elementType, int testJobId) throws InterruptedScanException;
}
