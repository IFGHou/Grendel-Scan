package com.grendelscan.smashers.categorizers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedSet;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.SingleSetCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.TestType;
import com.grendelscan.smashers.TransactionCategorizer;

/**
 * This categorizer is for a category tracked by tokens, for example, a file name. The tests are only performed once per unique token.
 * 
 * @author David Byrne
 */
public abstract class ByTokenCategorizer<T extends Token> extends SingleSetCategorizer implements TransactionCategorizer
{
    protected DatabaseBackedSet<String> tokensHashes;

    public ByTokenCategorizer(final Class<? extends TestType> categoryTestClass)
    {
        super(categoryTestClass);
        tokensHashes = new DatabaseBackedSet<String>(categoryTestClass.toString() + "_Tokens");
    }

    @Override
    public Map<AbstractSmasher, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();

        for (T token : getTokens(transaction))
        {
            String tokenHash = token.getTokenHash();
            if (!tokensHashes.contains(tokenHash))
            {
                for (AbstractSmasher module : testModules)
                {
                    for (TestJob testJob : makeTestJobs(transaction, module, token))
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

    protected abstract Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, AbstractSmasher module, T token);
}
