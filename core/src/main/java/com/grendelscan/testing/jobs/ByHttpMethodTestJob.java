package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHttpMethodTest;

public class ByHttpMethodTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHttpMethodTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHttpMethodTest) getModule()).testByHttpMethod(transactionID, getId());
    }
}
