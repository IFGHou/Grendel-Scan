package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.MultiSetTransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.jobs.ByHttpResponseCodeTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHttpResponseCodeTest;

public class ByHttpResponseCodeCategorizer extends MultiSetTransactionCategorizer
{
    public ByHttpResponseCodeCategorizer()
    {
        super(ByHttpResponseCodeTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractTestModule module)
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
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractTestModule module, @SuppressWarnings("unused") final String type)
    {
        TestJob testJob = new ByHttpResponseCodeTestJob(module.getClass(), transaction.getId());
        return testJob;
    }

}
