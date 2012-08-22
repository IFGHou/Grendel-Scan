package com.grendelscan.tests.testJobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;

public class ByHttpResponseCodeTestJob extends TransactionTestJob implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public ByHttpResponseCodeTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByHttpResponseCodeTest) getModule()).testByHttpResponseCode(transactionID, this.getId());
	}
}
