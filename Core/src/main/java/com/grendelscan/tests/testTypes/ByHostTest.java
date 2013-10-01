package com.grendelscan.tests.testTypes;

import com.grendelscan.scan.InterruptedScanException;

public interface ByHostTest extends TestType
{
	public void testByServer(int transactionID, int testJobId) throws InterruptedScanException;
}
