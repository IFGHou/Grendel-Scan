package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByHttpQueryTest extends TestType
{
	public void testByQuery(int transactionID, int testJobId) throws InterruptedScanException;
}
