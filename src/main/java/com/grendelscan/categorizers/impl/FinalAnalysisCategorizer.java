package com.grendelscan.categorizers.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.SingleSetCategorizer;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testJobs.FinalAnalysisTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.FinalAnalysisTest;

/**
 * 
 * @author David Byrne
 */
public class FinalAnalysisCategorizer extends SingleSetCategorizer 
{

	/** Creates a new instance of AllTransactionsCategorizer */
	public FinalAnalysisCategorizer()
	{
		super(FinalAnalysisTest.class);
	}

	public void runAnalysis()
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();

		for (TestModule module: testModules)
		{
			FinalAnalysisTestJob testJob = new FinalAnalysisTestJob(module.getClass());
			addJobToCollection(testJob, module, tests);
		}
		Scan.getInstance().getTesterQueue().submitJobs(tests);
	}

}
