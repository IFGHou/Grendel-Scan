/**
 * 
 */
package com.grendelscan.tests.testTypes;

import com.grendelscan.requester.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.scan.InterruptedScanException;

public interface ByQueryNamedDataTest extends TestType
{
	public void testByQueryNamedData(int transactionId, NameValuePairDataContainer datum, int testJobId) throws InterruptedScanException;
}
