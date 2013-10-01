package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByHostTest extends TestType
{
	public void testByServer(int transactionID, int testJobId) throws InterruptedScanException;
}
