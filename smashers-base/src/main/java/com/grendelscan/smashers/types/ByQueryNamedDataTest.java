/**
 * 
 */
package com.grendelscan.smashers.types;

import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.TestType;

public interface ByQueryNamedDataTest extends TestType
{
	public void testByQueryNamedData(int transactionId, NameValuePairDataContainer datum, int testJobId) throws InterruptedScanException;
}
