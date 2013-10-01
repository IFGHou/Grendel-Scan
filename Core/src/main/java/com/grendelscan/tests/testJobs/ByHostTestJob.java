package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHostTest;

public class ByHostTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ByHostTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByHostTest) getModule()).testByServer(transactionID, this.getId());
	}
}
