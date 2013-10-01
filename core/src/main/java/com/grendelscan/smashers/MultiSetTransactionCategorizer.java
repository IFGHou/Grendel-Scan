package com.grendelscan.smashers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;

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
    public Map<AbstractSmasher, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();
        for (String type : getTransactionTypeStrings(transaction))
        {
            type = type.toUpperCase();
            if (modulesByType.containsKey(type))
            {
                for (AbstractSmasher module : modulesByType.get(type))
                {
                    TestJob testJob = makeTestJob(transaction, module, type);
                    addJobToCollection(testJob, module, tests);
                }
            }
        }
        return tests;
    }

    protected abstract Set<String> getTransactionTypeStrings(StandardHttpTransaction transaction) throws InterruptedScanException;

    protected abstract TestJob makeTestJob(StandardHttpTransaction transaction, AbstractSmasher module, String type) throws InterruptedScanException;

}
