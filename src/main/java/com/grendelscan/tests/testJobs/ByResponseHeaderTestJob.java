package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByResponseHeaderTest;

public class ByResponseHeaderTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String	ResponseHeader;

	public ByResponseHeaderTestJob(Class<? extends TestModule> moduleClass, int transactionID, String responseHeader)
	{
		super(moduleClass, transactionID);
		this.ResponseHeader = responseHeader;
		this.transactionID = transactionID;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByResponseHeaderTest) getModule()).testByResponseHeader(transactionID, ResponseHeader, this.getId());
	}
}
