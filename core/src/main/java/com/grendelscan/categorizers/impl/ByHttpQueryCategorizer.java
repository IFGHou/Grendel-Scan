package com.grendelscan.categorizers.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.categorizers.tokens.QueryToken;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.ByHttpQueryTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHttpQueryTest;


/**
 * 
 * @author Administrator
 */
public class ByHttpQueryCategorizer extends ByTokenCategorizer<QueryToken>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ByHttpQueryCategorizer.class);

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
			LOGGER.error(e.toString(), e);
			throw ise;
		}
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, AbstractTestModule module, @SuppressWarnings("unused") QueryToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByHttpQueryTestJob(module.getClass(), transaction.getId()));
		return jobs;
	}
}
