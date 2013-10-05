package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.tokens.RequestDataLocationToken;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.data.ByteData;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.ByMutableRequestDataTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByRequestDataLocationTest;

public class ByRequestDataLocationCategorizer extends ByTokenCategorizer<RequestDataLocationToken>
{

    public ByRequestDataLocationCategorizer()
    {
        super(ByRequestDataLocationTest.class);
    }

    @Override
    protected List<RequestDataLocationToken> getTokens(final StandardHttpTransaction transaction)
    {
        List<RequestDataLocationToken> tokens = new ArrayList<RequestDataLocationToken>(1);
        for (Data datum : DataContainerUtils.getAllMutableDataDescendents(transaction.getTransactionContainer()))
        {
            if (datum instanceof ByteData)
            {
                tokens.add(new RequestDataLocationToken(datum.getReferenceChain()));
            }
        }
        return tokens;
    }

    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, final RequestDataLocationToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        // jobs.add(new ByMutableRequestDataTestJob(module.getClass(), transaction.getId(), (ByteData) token.getDatum()));
        jobs.add(new ByMutableRequestDataTestJob(module.getClass(), transaction.getId(), token.getChain()));
        return jobs;
    }
}
