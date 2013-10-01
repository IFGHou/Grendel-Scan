package com.grendelscan.tests.testTypes;

import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.scan.InterruptedScanException;

public interface ByRequestDataLocationTest extends TestType
{
//	public void testByRequestData(int transactionId, ByteData datum, int testJobId) throws InterruptedScanException;
	public void testByRequestData(int transactionId, DataReferenceChain chain, int testJobId) throws InterruptedScanException;
}
