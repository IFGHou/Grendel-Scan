package com.grendelscan.tests.testTypes;

import com.grendelscan.scan.InterruptedScanException;

public interface ByDirectoryTest extends TestType
{
	public void testByDirectory(int transactionID, String directory, int testJobId) throws InterruptedScanException;
}
