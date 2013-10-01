package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.smashers.SingleSetCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.TransactionCategorizer;
import com.grendelscan.smashers.jobs.AllTransactionsTestJob;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.AllTransactionsTest;

/**
 * 
 * @author David Byrne
 */
public class AllTransactionsCategorizer extends SingleSetCategorizer implements TransactionCategorizer
{

    /** Creates a new instance of AllTransactionsCategorizer */
    public AllTransactionsCategorizer()
    {
        super(AllTransactionsTest.class);
    }

    @Override
    public Map<AbstractSmasher, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction)
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();

        for (AbstractSmasher module : testModules)
        {
            AllTransactionsTestJob testJob = new AllTransactionsTestJob(module.getClass(), transaction.getId());
            addJobToCollection(testJob, module, tests);
        }
        return tests;
    }
}
