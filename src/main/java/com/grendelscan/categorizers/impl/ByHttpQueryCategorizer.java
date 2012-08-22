package com.grendelscan.categorizers.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.QueryToken;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByHttpQueryTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHttpQueryTest;


/**
 * 
 * @author Administrator
 */
public class ByHttpQueryCategorizer extends ByTokenCategorizer<QueryToken>
{

	public ByHttpQueryCategorizer()
	{
		super(ByHttpQueryTest.class);
	}

	@Override
	protected List<QueryToken> getTokens(StandardHttpTransaction transaction)
	{
		List<QueryToken> tokens = new ArrayList<QueryToken>(1);
		
		try
		{
			tokens.add(new QueryToken(transaction.getRequestWrapper().getAbsoluteUriString()));
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, @SuppressWarnings("unused") QueryToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByHttpQueryTestJob(module.getClass(), transaction.getId()));
		return jobs;
	}
}
