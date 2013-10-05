package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByHostTest extends TestType
{
	public void testByServer(int transactionID, int testJobId) throws InterruptedScanException;
}
