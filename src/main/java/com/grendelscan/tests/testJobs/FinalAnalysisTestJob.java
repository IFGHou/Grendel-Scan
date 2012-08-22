package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.FinalAnalysisTest;

public class FinalAnalysisTestJob extends TestJob
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public FinalAnalysisTestJob(Class<? extends TestModule> moduleClass)
	{
		super(moduleClass);
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((FinalAnalysisTest) getModule()).runAnalysis(this.getId());
	}

}
