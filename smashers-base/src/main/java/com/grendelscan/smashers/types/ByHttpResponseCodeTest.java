package com.grendelscan.smashers.types;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

/**
 * 
 * @author Administrator
 */
public interface ByHttpResponseCodeTest extends TestType
{
	public String[] getResponseCodes();

	public void testByHttpResponseCode(int transactionID, int testJobId) throws InterruptedScanException;
}
