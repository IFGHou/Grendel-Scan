package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpQueryTest;

public class ByHttpQueryTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ByHttpQueryTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByHttpQueryTest) getModule()).testByQuery(transactionID, this.getId());
	}
}
