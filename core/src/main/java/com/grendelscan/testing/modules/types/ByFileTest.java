package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByFileTest extends TestType
{
	public void testByFile(int transactionID, int testJobId) throws InterruptedScanException;
}
