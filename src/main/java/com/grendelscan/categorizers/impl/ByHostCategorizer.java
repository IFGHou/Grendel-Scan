package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.StringToken;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByHostTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHostTest;


/**
 * 
 * @author David Byrne
 */
public class ByHostCategorizer extends ByTokenCategorizer<StringToken>
{

	public ByHostCategorizer()
	{
		super(ByHostTest.class);
	}

	@Override
	protected List<StringToken> getTokens(StandardHttpTransaction transaction)
	{
		List<StringToken> tokens = new ArrayList<StringToken>(1);
		tokens.add(new StringToken(transaction.getRequestWrapper().getHost())); 
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, @SuppressWarnings("unused") StringToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByHostTestJob(module.getClass(), transaction.getId()));
		return jobs;
	}
}
