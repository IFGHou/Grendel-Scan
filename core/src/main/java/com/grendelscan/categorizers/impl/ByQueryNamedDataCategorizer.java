/**
 * 
 */
package com.grendelscan.categorizers.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grendelscan.categorizers.tokens.RequestDataValueToken;
import com.grendelscan.commons.http.dataHandling.containers.DataContainerUtils;
import com.grendelscan.commons.http.dataHandling.containers.NameValuePairDataContainer;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.ByQueryNamedDataTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByQueryNamedDataTest;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.ByTokenCategorizer#getTokens(com.grendelscan.commons.http.transactions.StandardHttpTransaction)
     */
    @Override
    protected List<RequestDataValueToken> getTokens(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        List<RequestDataValueToken> tokens = new ArrayList<RequestDataValueToken>();
        for (Data datum : DataContainerUtils.getAllDataDescendents(transaction.getTransactionContainer()))
        {
            if (datum instanceof NameValuePairDataContainer)
            {
                tokens.add(new RequestDataValueToken(datum));
            }
        }
        return tokens;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.categorizers.ByTokenCategorizer#makeTestJobs(com.grendelscan.commons.http.transactions.StandardHttpTransaction, com.grendelscan.smashers.AbstractSmasher,
     * com.grendelscan.categorizers.tokens.Token)
     */
    @Override
    protected Set<TestJob> makeTestJobs(final StandardHttpTransaction transaction, final AbstractTestModule module, final RequestDataValueToken token)
    {
        Set<TestJob> jobs = new HashSet<TestJob>();
        // Data datum = DataContainerUtils.resolveReferenceChain(transaction.getTransactionContainer(), token.getChain());
        jobs.add(new ByQueryNamedDataTestJob(module.getClass(), transaction.getId(), (NameValuePairDataContainer) token.getDatum()));
        return jobs;
    }

}
