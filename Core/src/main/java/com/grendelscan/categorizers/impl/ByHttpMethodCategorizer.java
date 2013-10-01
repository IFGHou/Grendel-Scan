package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.MultiSetTransactionCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByHttpMethodTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpMethodTest;

public class ByHttpMethodCategorizer extends MultiSetTransactionCategorizer
{
	public ByHttpMethodCategorizer()
	{
		super(ByHttpMethodTest.class);
	}

	@Override
	public String[] getModuleTypes(TestModule module)
	{
		ByHttpMethodTest test = (ByHttpMethodTest) module;
		return test.getHttpMethods();
	}

	@Override
	protected Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction)
	{
		Set<String> types = new HashSet<String>(1);
		types.add(transaction.getRequestWrapper().getMethod());
		return types;
	}

	@Override
	protected TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, @SuppressWarnings("unused") String type)
	{
		TestJob testJob = new ByHttpMethodTestJob(module.getClass(), transaction.getId());
		return testJob;
	}

}
