package com.grendelscan.smashers.categorizers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByHostTestJob;
import com.grendelscan.smashers.types.ByHostTest;

/**
 * 
 * @author David Byrne
 */
public class ByHostCategorizer extends ByTokenCategorizer<StringToken>
{

    public ByHostCategorizer()
    {
        super(ByHostTest.class);
    }

    @Override
    protected List<StringToken> getTokens(final StandardHttpTransaction transaction)
    {
        List<StringToken> tokens = new ArrayList<StringToken>(1);
        tokens.add(new StringToken(transaction.getRequestWrapper().getHost()));
        return tokens;
    }

    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractSmasher module, @SuppressWarnings("unused") final StringToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        jobs.add(new ByHostTestJob(module.getClass(), transaction.getId()));
        return jobs;
    }
}
