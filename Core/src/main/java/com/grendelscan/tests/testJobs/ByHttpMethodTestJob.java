package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpMethodTest;

public class ByHttpMethodTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ByHttpMethodTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByHttpMethodTest) getModule()).testByHttpMethod(transactionID, this.getId());
	}
}
