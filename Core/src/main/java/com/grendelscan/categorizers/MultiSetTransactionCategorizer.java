package com.grendelscan.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.TestType;


/**
 * The type strings are not case-sensitive.
 * 
 * @author David Byrne
 * 
 */
public abstract class MultiSetTransactionCategorizer extends MultiSetCategorizer implements TransactionCategorizer
{
	public MultiSetTransactionCategorizer(Class<? extends TestType> categoryTestClass)
	{
		super(categoryTestClass);
	}

	@Override
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();
		for (String type: getTransactionTypeStrings(transaction))
		{
			type = type.toUpperCase();
			if (modulesByType.containsKey(type))
			{
				for (TestModule module: modulesByType.get(type))
				{
					TestJob testJob = makeTestJob(transaction, module, type);
					addJobToCollection(testJob, module, tests);
				}
			}
		}
		return tests;
	}

	protected abstract Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction) throws InterruptedScanException;

	protected abstract TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, String type) throws InterruptedScanException;

}
