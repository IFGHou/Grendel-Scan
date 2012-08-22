package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByDirectoryTest;

public class ByDirectoryTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String	directory;

	public ByDirectoryTestJob(Class<? extends TestModule> moduleClass, int transactionID, String directory)
	{
		super(moduleClass, transactionID);
		this.directory = directory;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByDirectoryTest) getModule()).testByDirectory(transactionID, directory, this.getId());
	}
}
