package com.grendelscan.categorizers.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;

import com.grendelscan.categorizers.interfaces.MultiSetTransactionCategorizer;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.testing.jobs.ByResponseHeaderTestJob;
import com.grendelscan.testing.jobs.TestJob;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByResponseHeaderTest;

public class ByResponseHeaderCategorizer extends MultiSetTransactionCategorizer
{
    public ByResponseHeaderCategorizer()
    {
        super(ByResponseHeaderTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractTestModule module)
    {
        ByResponseHeaderTest test = (ByResponseHeaderTest) module;
        return test.getResponseHeaders();
    }

    @Override
    protected Set<String> getTransactionTypeStrings(final StandardHttpTransaction transaction)
    {
        // If we don't make these unique, duplicate test jobs will be created.
        Set<String> uniqueNames = new HashSet<String>();
        for (Header header : transaction.getResponseWrapper().getHeaders().getReadOnlyHeaders())
        {
            uniqueNames.add(header.getName());
        }
        return uniqueNames;
    }

    @Override
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractTestModule module, final String type)
    {
        TestJob testJob = new ByResponseHeaderTestJob(module.getClass(), transaction.getId(), type);
        return testJob;
    }
}
