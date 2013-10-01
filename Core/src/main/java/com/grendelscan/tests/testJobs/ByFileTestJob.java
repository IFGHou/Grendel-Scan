package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByFileTest;

public class ByFileTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ByFileTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByFileTest) getModule()).testByFile(transactionID, this.getId());
	}
}
