package com.grendelscan.tests.testJobs;

import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataTest;

public class ByMutableRequestDataTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private MutableData datum;

	public ByMutableRequestDataTestJob(Class<? extends TestModule> moduleClass, int transactionID, MutableData datum)
	{
		super(moduleClass, transactionID);
		this.datum = datum;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByRequestDataTest) getModule()).testByRequestData(getTransactionID(), datum, this.getId());
	}
}
