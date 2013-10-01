package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.MultiSetTransactionCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByMimeTypeTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByMimeTypeTest;

public class ByMimeTypeCategorizer extends MultiSetTransactionCategorizer
{
	public ByMimeTypeCategorizer()
	{
		super(ByMimeTypeTest.class);
	}

	@Override
	public String[] getModuleTypes(TestModule module)
	{
		ByMimeTypeTest test = (ByMimeTypeTest) module;
		return test.getMimeTypes();
	}

	@Override
	protected Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction)
	{
		Set<String> types = new HashSet<String>(1);
		types.add(transaction.getResponseWrapper().getHeaders().getMimeType());
		return types;
	}

	@Override
	protected TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, String type)
	{
		TestJob testJob = new ByMimeTypeTestJob(module.getClass(), transaction.getId(), type);
		return testJob;
	}

}
