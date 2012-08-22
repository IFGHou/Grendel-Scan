package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;

import com.grendelscan.categorizers.MultiSetTransactionCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByResponseHeaderTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByResponseHeaderTest;

public class ByResponseHeaderCategorizer extends MultiSetTransactionCategorizer
{
	public ByResponseHeaderCategorizer()
	{
		super(ByResponseHeaderTest.class);
	}

	@Override
	public String[] getModuleTypes(TestModule module)
	{
		ByResponseHeaderTest test = (ByResponseHeaderTest) module;
		return test.getResponseHeaders();
	}

	@Override
	protected Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction)
	{
		// If we don't make these unique, duplicate test jobs will be created.
		Set<String> uniqueNames = new HashSet<String>();
		for (Header header: transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
		{
			uniqueNames.add(header.getName());
		}
		return uniqueNames;
	}

	@Override
	protected TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, String type)
	{
		TestJob testJob = new ByResponseHeaderTestJob(module.getClass(), transaction.getId(), type);
		return testJob;
	}
}
