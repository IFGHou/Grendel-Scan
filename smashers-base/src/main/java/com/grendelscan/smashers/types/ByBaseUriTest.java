package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByBaseUriTest extends TestType
{
	public void testByBaseUri(String baseUri, int testJobId) throws InterruptedScanException;
}
