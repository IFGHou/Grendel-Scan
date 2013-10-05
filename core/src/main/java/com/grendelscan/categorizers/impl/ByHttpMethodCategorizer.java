package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.MultiSetTransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.ByHttpMethodTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHttpMethodTest;

public class ByHttpMethodCategorizer extends MultiSetTransactionCategorizer
{
    public ByHttpMethodCategorizer()
    {
        super(ByHttpMethodTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractTestModule module)
    {
        ByHttpMethodTest test = (ByHttpMethodTest) module;
        return test.getHttpMethods();
    }

    @Override
    protected Set<String> getTransactionTypeStrings(final StandardHttpTransaction transaction)
    {
        Set<String> types = new HashSet<String>(1);
        types.add(transaction.getRequestWrapper().getMethod());
        return types;
    }

    @Override
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractTestModule module, @SuppressWarnings("unused") final String type)
    {
        TestJob testJob = new ByHttpMethodTestJob(module.getClass(), transaction.getId());
        return testJob;
    }

}
