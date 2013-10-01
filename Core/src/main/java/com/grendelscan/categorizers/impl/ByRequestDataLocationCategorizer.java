package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.RequestDataLocationToken;
import com.grendelscan.categorizers.tokens.RequestDataValueToken;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.tests.testJobs.ByMutableRequestDataTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRequestDataLocationTest;


public class ByRequestDataLocationCategorizer extends ByTokenCategorizer<RequestDataLocationToken>
{

	public ByRequestDataLocationCategorizer()
	{
		super(ByRequestDataLocationTest.class);
	}

	@Override
	protected List<RequestDataLocationToken> getTokens(StandardHttpTransaction transaction)
	{
		List<RequestDataLocationToken> tokens = new ArrayList<RequestDataLocationToken>(1);
		for(Data datum: DataContainerUtils.getAllMutableDataDescendents(transaction.getTransactionContainer()))
		{
			if (datum instanceof ByteData)
			{
				tokens.add(new RequestDataLocationToken(datum.getReferenceChain()));
			}
		}
		return tokens;
	}

	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, RequestDataLocationToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
//		jobs.add(new ByMutableRequestDataTestJob(module.getClass(), transaction.getId(), (ByteData) token.getDatum()));
		jobs.add(new ByMutableRequestDataTestJob(module.getClass(), transaction.getId(), token.getChain()));
		return jobs;
	}}
