package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByHttpMethodTest extends TestType
{
	public String[] getHttpMethods();

	public void testByHttpMethod(int transactionID, int testJobId) throws InterruptedScanException;
}
