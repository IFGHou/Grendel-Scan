package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.MultiSetTransactionCategorizer;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.ByHttpResponseCodeTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpResponseCodeTest;

public class ByHttpResponseCodeCategorizer extends MultiSetTransactionCategorizer
{
	public ByHttpResponseCodeCategorizer()
	{
		super(ByHttpResponseCodeTest.class);
	}

	@Override
	public String[] getModuleTypes(TestModule module)
	{
		ByHttpResponseCodeTest test = (ByHttpResponseCodeTest) module;
		return test.getResponseCodes();
	}

	@Override
	protected Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Set<String> types = new HashSet<String>(1);
		types.add(String.valueOf(transaction.getLogicalResponseCode()));
		return types;
	}

	@Override
	protected TestJob makeTestJob(StandardHttpTransaction transaction, TestModule module, @SuppressWarnings("unused") String type)
	{
		TestJob testJob = new ByHttpResponseCodeTestJob(module.getClass(), transaction.getId());
		return testJob;
	}

}
