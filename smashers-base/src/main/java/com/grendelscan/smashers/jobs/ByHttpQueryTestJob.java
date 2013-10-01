package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByHttpQueryTest;

public class ByHttpQueryTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHttpQueryTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHttpQueryTest) getModule()).testByQuery(transactionID, getId());
    }
}
