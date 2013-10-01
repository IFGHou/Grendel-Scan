package com.grendelscan.categorizers.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.SingleSetCategorizer;
import com.grendelscan.categorizers.TransactionCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.AllTransactionsTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.AllTransactionsTest;
/**
 * 
 * @author David Byrne
 */
public class AllTransactionsCategorizer extends SingleSetCategorizer implements TransactionCategorizer
{

	/** Creates a new instance of AllTransactionsCategorizer */
	public AllTransactionsCategorizer()
	{
		super(AllTransactionsTest.class);
	}

	@Override
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction)
	{
		Map<TestModule, Set<TestJob>>  tests = new HashMap<TestModule, Set<TestJob>> ();
		
		for (TestModule module: testModules)
		{
			AllTransactionsTestJob testJob = new AllTransactionsTestJob(module.getClass(), transaction.getId());
			addJobToCollection(testJob, module, tests);
		}
		return tests;
	}
}
