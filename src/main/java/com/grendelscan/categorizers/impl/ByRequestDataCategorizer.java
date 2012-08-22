package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.RequestDataToken;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.dataHandling.data.MutableData;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByMutableRequestDataTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataTest;


public class ByRequestDataCategorizer extends ByTokenCategorizer<RequestDataToken>
{

	public ByRequestDataCategorizer()
	{
		super(ByRequestDataTest.class);
	}

	@Override
	protected List<RequestDataToken> getTokens(StandardHttpTransaction transaction)
	{
		List<RequestDataToken> tokens = new ArrayList<RequestDataToken>(1);
		for(Data datum: DataContainerUtils.getAllDataDescendents(transaction.getTransactionContainer()))
		{
			if (datum instanceof MutableData)
			{
				tokens.add(new RequestDataToken(datum));
			}
		}
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, RequestDataToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByMutableRequestDataTestJob(module.getClass(), transaction.getId(), (MutableData) token.getDatum()));
		return jobs;
	}}
