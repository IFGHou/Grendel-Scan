package com.grendelscan.categorizers.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.TestType;

/**
 * The type strings are not case-sensitive.
 * 
 * @author David Byrne
 * 
 */
public abstract class MultiSetTransactionCategorizer extends MultiSetCategorizer implements TransactionCategorizer
{
    public MultiSetTransactionCategorizer(final Class<? extends TestType> categoryTestClass)
    {
        super(categoryTestClass);
    }

    @Override
    public Map<AbstractTestModule, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();
        for (String type : getTransactionTypeStrings(transaction))
        {
            type = type.toUpperCase();
            if (modulesByType.containsKey(type))
            {
                for (AbstractTestModule module : modulesByType.get(type))
                {
                    TestJob testJob = makeTestJob(transaction, module, type);
                    addJobToCollection(testJob, module, tests);
                }
            }
        }
        return tests;
    }

    protected abstract Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction) throws InterruptedScanException;

    protected abstract TestJob makeTestJob(StandardHttpTransaction transaction, AbstractTestModule module, String type) throws InterruptedScanException;

}
