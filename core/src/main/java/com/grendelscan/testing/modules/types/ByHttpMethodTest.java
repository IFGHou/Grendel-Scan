package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByHttpMethodTest extends TestType
{
	public String[] getHttpMethods();

	public void testByHttpMethod(int transactionID, int testJobId) throws InterruptedScanException;
}
