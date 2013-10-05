package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByHttpQueryTest extends TestType
{
	public void testByQuery(int transactionID, int testJobId) throws InterruptedScanException;
}
