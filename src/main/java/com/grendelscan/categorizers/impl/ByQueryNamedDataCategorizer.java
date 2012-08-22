/**
 * 
 */
package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.RequestDataToken;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.requester.http.dataHandling.data.Data;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.ByQueryNamedDataTestJob;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByQueryNamedDataTest;

/**
 * @author david
 *
 */
public class ByQueryNamedDataCategorizer extends ByTokenCategorizer<RequestDataToken>
{

	/**
	 * @param categoryTestClass
	 */
	public ByQueryNamedDataCategorizer()
	{
		super(ByQueryNamedDataTest.class);
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.ByTokenCategorizer#getTokens(com.grendelscan.requester.http.transactions.StandardHttpTransaction)
	 */
	@Override
	protected List<RequestDataToken> getTokens(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		List<RequestDataToken> tokens = new ArrayList<RequestDataToken>();
		for(Data datum: DataContainerUtils.getAllDataDescendents(transaction.getTransactionContainer()))
		{
			if (datum instanceof NamedDataContainer)
				tokens.add(new RequestDataToken(datum));
		}
		return tokens;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.ByTokenCategorizer#makeTestJobs(com.grendelscan.requester.http.transactions.StandardHttpTransaction, com.grendelscan.tests.testModules.TestModule, com.grendelscan.categorizers.tokens.Token)
	 */
	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, RequestDataToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
		jobs.add(new ByQueryNamedDataTestJob(module.getClass(), transaction.getId(), (NamedDataContainer) token.getDatum()));
		return jobs;
	}

}
