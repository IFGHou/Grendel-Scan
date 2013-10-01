package com.grendelscan.categorizers.impl;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.scan.Scan;
import com.grendelscan.tests.libraries.TokenTesting.DiscoveredContexts;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.libraries.TokenTesting.TokenContextType;
import com.grendelscan.tests.testJobs.ByRepeatableOutputContextTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRepeatableOutputContextTest;

public class ByRepeatableOutputContextCategorizer extends ByOutputContextCategorizer
{

	public ByRepeatableOutputContextCategorizer()
	{
		super(ByRepeatableOutputContextTest.class);

	}

	public void analyzeRepeatableOutputContexts(DiscoveredContexts contexts, int transactionID)
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();
		createJobs(contexts, transactionID, tests);
		Scan.getInstance().getTesterQueue().submitJobs(tests);
	}

	
	@Override
	public TokenContextType[] getModuleTypes(TestModule module)
	{
		return ((ByRepeatableOutputContextTest) module).getDesiredRepeatableContexts();
	}

	@Override
	protected TestJob createTestJob(Class<? extends TestModule> moduleClass, int transactionId, Collection<TokenContext> contexts)
	{
		return new ByRepeatableOutputContextTestJob(moduleClass, transactionId, contexts);
	}

}
