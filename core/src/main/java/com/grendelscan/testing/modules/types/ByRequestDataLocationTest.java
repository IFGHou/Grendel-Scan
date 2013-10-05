package com.grendelscan.testing.modules.types;

import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.scan.InterruptedScanException;

public interface ByRequestDataLocationTest extends TestType
{
//	public void testByRequestData(int transactionId, ByteData datum, int testJobId) throws InterruptedScanException;
	public void testByRequestData(int transactionId, DataReferenceChain chain, int testJobId) throws InterruptedScanException;
}
