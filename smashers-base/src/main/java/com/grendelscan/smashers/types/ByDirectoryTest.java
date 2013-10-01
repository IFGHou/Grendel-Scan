package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByDirectoryTest extends TestType
{
	public void testByDirectory(int transactionID, String directory, int testJobId) throws InterruptedScanException;
}
