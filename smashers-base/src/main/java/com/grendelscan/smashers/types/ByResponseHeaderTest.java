package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByResponseHeaderTest extends TestType
{
	public String[] getResponseHeaders();

	public void testByResponseHeader(int transactionID, String responseHeaderName, int testJobId) throws InterruptedScanException;
}
