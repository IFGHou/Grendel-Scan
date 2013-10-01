package com.grendelscan.tests.testTypes;

import com.grendelscan.scan.InterruptedScanException;

public interface ByBaseUriTest extends TestType
{
	public void testByBaseUri(String baseUri, int testJobId) throws InterruptedScanException;
}
