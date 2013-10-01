package com.grendelscan.tests.testTypes;

import com.grendelscan.scan.InterruptedScanException;

public interface ByFileTest extends TestType
{
	public void testByFile(int transactionID, int testJobId) throws InterruptedScanException;
}
