package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByMimeTypeTest extends TestType
{
	public String[] getMimeTypes();

	public void testByMimeType(int transactionID, String mimeType, int testJobId) throws InterruptedScanException;
}
