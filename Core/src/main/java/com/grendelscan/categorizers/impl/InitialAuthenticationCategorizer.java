package com.grendelscan.categorizers.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.SingleSetCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.SessionIDTesting.SessionIDLocation;
import com.grendelscan.tests.testJobs.InitialAuthenticationTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.InitialAuthenticationTest;

/**
 * 
 * @author David Byrne
 */
public class InitialAuthenticationCategorizer extends SingleSetCategorizer
{

	/** Creates a new instance of InitialAuthenticationCategorizer */
	public InitialAuthenticationCategorizer()
	{
		super(InitialAuthenticationTest.class);
	}

	public void analyzeAuthentication(StandardHttpTransaction transaction, SessionIDLocation sessionIDLocation)
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();
		for (TestModule module: testModules)
		{
			InitialAuthenticationTestJob testJob = new InitialAuthenticationTestJob(module.getClass(), transaction.getId(), sessionIDLocation);
			addJobToCollection(testJob, module, tests);
		}
		Scan.getInstance().getTesterQueue().submitJobs(tests);
	}
}