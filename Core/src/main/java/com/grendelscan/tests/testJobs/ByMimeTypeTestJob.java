package com.grendelscan.tests.testJobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByMimeTypeTest;

public class ByMimeTypeTestJob extends TransactionTestJob implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String	mimeType;

	public ByMimeTypeTestJob(Class<? extends TestModule> moduleClass, int transactionID, String mimeType)
	{
		super(moduleClass, transactionID);
		this.mimeType = mimeType;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByMimeTypeTest) getModule()).testByMimeType(transactionID, mimeType, this.getId());
	}
}
