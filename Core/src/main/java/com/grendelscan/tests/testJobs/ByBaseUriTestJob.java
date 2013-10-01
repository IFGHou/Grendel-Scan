package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByBaseUriTest;

public class ByBaseUriTestJob extends TestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String baseUri;

	public ByBaseUriTestJob(Class<? extends TestModule> moduleClass, String baseUri)
	{
		super(moduleClass);
		this.baseUri = baseUri;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByBaseUriTest) getModule()).testByBaseUri(baseUri, this.getId());
	}
}
