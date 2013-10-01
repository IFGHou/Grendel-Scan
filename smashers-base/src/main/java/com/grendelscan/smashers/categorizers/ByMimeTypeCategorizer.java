package com.grendelscan.smashers.categorizers;

import java.util.HashSet;
import java.util.Set;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.MultiSetTransactionCategorizer;
import com.grendelscan.smashers.TestJob;
import com.grendelscan.smashers.jobs.ByMimeTypeTestJob;
import com.grendelscan.smashers.types.ByMimeTypeTest;

public class ByMimeTypeCategorizer extends MultiSetTransactionCategorizer
{
    public ByMimeTypeCategorizer()
    {
        super(ByMimeTypeTest.class);
    }

    @Override
    public String[] getModuleTypes(final AbstractSmasher module)
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
    protected TestJob makeTestJob(final StandardHttpTransaction transaction, final AbstractSmasher module, final String type)
    {
        TestJob testJob = new ByMimeTypeTestJob(module.getClass(), transaction.getId(), type);
        return testJob;
    }

}
