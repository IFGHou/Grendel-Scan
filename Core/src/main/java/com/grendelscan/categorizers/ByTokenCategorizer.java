package com.grendelscan.categorizers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.tokens.Token;
import com.grendelscan.data.database.collections.DatabaseBackedSet;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testJobs.TestJob;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.TestType;

/**
 * This categorizer is for a category tracked by tokens, for example, a file name.
 * The tests are only performed once per unique token.
 * @author David Byrne
 */
public abstract class ByTokenCategorizer<T extends Token> extends SingleSetCategorizer implements TransactionCategorizer
{
	protected DatabaseBackedSet<String> tokensHashes;

	public ByTokenCategorizer(Class<? extends TestType> categoryTestClass)
	{
		super(categoryTestClass);
		tokensHashes = new DatabaseBackedSet<String>(categoryTestClass.toString() + "_Tokens");
	}

	@Override
	public Map<TestModule, Set<TestJob>> analyzeTransaction(StandardHttpTransaction transaction) throws InterruptedScanException
	{
		Map<TestModule, Set<TestJob>> tests = new HashMap<TestModule, Set<TestJob>>();

		for (T token: getTokens(transaction))
		{
			String tokenHash = token.getTokenHash();
			if (!tokensHashes.contains(tokenHash))
			{
				for (TestModule module: testModules)
				{
					for (TestJob testJob: makeTestJobs(transaction, module, token))
					{
						addJobToCollection(testJob, module, tests);
					}
				}
				tokensHashes.add(tokenHash);
			}
		}
		return tests;
	}

	
	protected abstract List<T> getTokens(StandardHttpTransaction transaction) throws InterruptedScanException;
	protected abstract Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, TestModule module, T token);
}
