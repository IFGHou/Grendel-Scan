package com.grendelscan.tests.testJobs;

import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.references.DataReferenceChain;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataLocationTest;

public class ByMutableRequestDataTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
//	private ByteData datum;
	private DataReferenceChain chain;
	
//	public ByMutableRequestDataTestJob(Class<? extends TestModule> moduleClass, int transactionID, ByteData datum)
	public ByMutableRequestDataTestJob(Class<? extends TestModule> moduleClass, int transactionID, DataReferenceChain chain)
	{
		super(moduleClass, transactionID);
		this.chain = chain;
//		this.datum = datum;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
//		((ByRequestDataTest) getModule()).testByRequestData(getTransactionID(), datum, this.getId());
		((ByRequestDataLocationTest) getModule()).testByRequestData(getTransactionID(), chain, this.getId());
	}
}
