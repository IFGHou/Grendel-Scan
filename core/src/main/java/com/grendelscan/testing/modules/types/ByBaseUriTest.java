package com.grendelscan.testing.modules.types;

import com.grendelscan.scan.InterruptedScanException;

public interface ByBaseUriTest extends TestType
{
	public void testByBaseUri(String baseUri, int testJobId) throws InterruptedScanException;
}
