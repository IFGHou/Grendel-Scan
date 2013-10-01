package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByFileTest extends TestType
{
	public void testByFile(int transactionID, int testJobId) throws InterruptedScanException;
}
