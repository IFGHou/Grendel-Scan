package com.grendelscan.tests.testTypes;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.scan.InterruptedScanException;

public interface ByRequestDataTest extends TestType
{
	public void testByRequestData(int transactionId, MutableData datum, int testJobId) throws InterruptedScanException;
}
