package com.grendelscan.smashers.categorizers;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.MultiSetTransactionCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByHttpMethodTestJob;
import com.grendelscan.smashers.types.ByHttpMethodTest;

public class ByHttpMethodCategorizer extends MultiSetTransactionCategorizer
{
    public ByHttpMethodCategorizer()
    {
        super(ByHttpMethodTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractSmasher module)
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
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractSmasher module, @SuppressWarnings("unused") final String type)
    {
        TestJob testJob = new ByHttpMethodTestJob(module.getClass(), transaction.getId());
        return testJob;
    }

}
