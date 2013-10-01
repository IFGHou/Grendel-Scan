package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByHttpMethodTest;

public class ByHttpMethodTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHttpMethodTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHttpMethodTest) getModule()).testByHttpMethod(transactionID, getId());
    }
}
