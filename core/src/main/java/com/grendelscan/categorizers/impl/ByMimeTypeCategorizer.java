package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.categorizers.interfaces.MultiSetTransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.ByMimeTypeTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByMimeTypeTest;

public class ByMimeTypeCategorizer extends MultiSetTransactionCategorizer
{
    public ByMimeTypeCategorizer()
    {
        super(ByMimeTypeTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractTestModule module)
    {
        ByMimeTypeTest test = (ByMimeTypeTest) module;
        return test.getMimeTypes();
    }

    @Override
    protected Set<String> getTransactionTypeStrings(final StandardHttpTransaction transaction)
    {
        Set<String> types = new HashSet<String>(1);
        types.add(transaction.getResponseWrapper().getHeaders().getMimeType());
        return types;
    }

    @Override
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractTestModule module, final String type)
    {
        TestJob testJob = new ByMimeTypeTestJob(module.getClass(), transaction.getId(), type);
        return testJob;
    }

}
