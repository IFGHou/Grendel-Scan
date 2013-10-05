/**
 * 
 */
package com.grendelscan.testing.modules.types;

import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.scan.InterruptedScanException;

public interface ByQueryNamedDataTest extends TestType
{
	public void testByQueryNamedData(int transactionId, NameValuePairDataContainer datum, int testJobId) throws InterruptedScanException;
}
