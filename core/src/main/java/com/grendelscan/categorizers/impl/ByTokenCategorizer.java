package com.grendelscan.categorizers.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.SingleSetCategorizer;
import com.grendelscan.categorizers.interfaces.TransactionCategorizer;
import com.grendelscan.categorizers.tokens.Token;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.data.database.collections.DatabaseBackedSet;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.TestType;

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
    public Map<AbstractTestModule, Set<TestJob>> analyzeTransaction(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Map<AbstractTestModule, Set<TestJob>> tests = new HashMap<AbstractTestModule, Set<TestJob>>();

        for (T token : getTokens(transaction))
        {
            String tokenHash = token.getTokenHash();
            if (!tokensHashes.contains(tokenHash))
            {
                for (AbstractTestModule module : testModules)
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

    protected abstract Set<TestJob> makeTestJobs(StandardHttpTransaction transaction, AbstractTestModule module, T token);
}
