package com.grendelscan.categorizers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.SingleSetCategorizer;
import com.grendelscan.categorizers.interfaces.TransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.AllTransactionsTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.AllTransactionsTest;

/**
 * 
 * @author David Byrne
 */
public class AllTransactionsCategorizer extends SingleSetCategorizer implements
		TransactionCategorizer {

	/** Creates a new instance of AllTransactionsCategorizer */
	public AllTransactionsCategorizer() {
		super(AllTransactionsTest.class);
	}

	@Override
	public Map<AbstractTestModule, Set<TestJob>> analyzeTransaction(
			final StandardHttpTransaction transaction) {
		Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();

		for (AbstractTestModule module : testModules) {
			AllTransactionsTestJob testJob = new AllTransactionsTestJob(
					module.getClass(), transaction.getId());
			addJobToCollection(testJob, module, tests);
		}
		return tests;
	}
}
