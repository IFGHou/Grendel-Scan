package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByHostTest;

public class ByHostTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHostTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHostTest) getModule()).testByServer(transactionID, getId());
    }
}
