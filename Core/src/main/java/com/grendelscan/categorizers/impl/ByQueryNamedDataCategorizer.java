/**
 * 
 */
package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.ByTokenCategorizer;
import com.grendelscan.categorizers.tokens.RequestDataValueToken;
import com.grendelscan.requester.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.requester.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.requester.http.dataHandling.data.ByteData;
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
public class ByQueryNamedDataCategorizer extends ByTokenCategorizer<RequestDataValueToken>
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
	protected List<RequestDataValueToken> getTokens(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		List<RequestDataValueToken> tokens = new ArrayList<RequestDataValueToken>();
		for(Data datum: DataContainerUtils.getAllDataDescendents(transaction.getTransactionContainer()))
		{
			if (datum instanceof NameValuePairDataContainer)
				tokens.add(new RequestDataValueToken(datum));
		}
		return tokens;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.categorizers.ByTokenCategorizer#makeTestJobs(com.grendelscan.requester.http.transactions.StandardHttpTransaction, com.grendelscan.tests.testModules.TestModule, com.grendelscan.categorizers.tokens.Token)
	 */
	@Override
	protected Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, RequestDataValueToken token)
	{
		Set<TestJob> jobs = new HashSet<TestJob>();
//		Data datum = DataContainerUtils.resolveReferenceChain(transaction.getTransactionContainer(), token.getChain());
		jobs.add(new ByQueryNamedDataTestJob(module.getClass(), transaction.getId(), (NameValuePairDataContainer) token.getDatum()));
		return jobs;
	}

}
