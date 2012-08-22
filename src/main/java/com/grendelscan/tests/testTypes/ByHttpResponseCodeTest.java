package com.grendelscan.tests.testTypes;

import com.grendelscan.scan.InterruptedScanException;

/**
 * 
 * @author Administrator
 */
public interface ByHttpResponseCodeTest extends TestType
{
	public String[] getResponseCodes();

	public void testByHttpResponseCode(int transactionID, int testJobId) throws InterruptedScanException;
}
