package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.AllTransactionsTest;

public class AllTransactionsTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AllTransactionsTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass, transactionID);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((AllTransactionsTest) getModule()).testAllTransactions(transactionID, this.getId());
	}
}
