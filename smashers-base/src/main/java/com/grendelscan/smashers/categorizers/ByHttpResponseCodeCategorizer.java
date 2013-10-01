package com.grendelscan.smashers.categorizers;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.MultiSetTransactionCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByHttpResponseCodeTestJob;
import com.grendelscan.smashers.types.ByHttpResponseCodeTest;

public class ByHttpResponseCodeCategorizer extends MultiSetTransactionCategorizer
{
    public ByHttpResponseCodeCategorizer()
    {
        super(ByHttpResponseCodeTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractSmasher module)
    {
        ByHttpResponseCodeTest test = (ByHttpResponseCodeTest) module;
        return test.getResponseCodes();
    }

    @Override
    protected Set<String> getTransactionTypeStrings(final StandardHttpTransaction transaction) throws InterruptedScanException
    {
        Set<String> types = new HashSet<String>(1);
        types.add(String.valueOf(transaction.getLogicalResponseCode()));
        return types;
    }

    @Override
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractSmasher module, @SuppressWarnings("unused") final String type)
    {
        TestJob testJob = new ByHttpResponseCodeTestJob(module.getClass(), transaction.getId());
        return testJob;
    }

}
