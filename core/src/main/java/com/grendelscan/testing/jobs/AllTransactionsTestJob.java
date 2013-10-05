package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.AllTransactionsTest;

public class AllTransactionsTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public AllTransactionsTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((AllTransactionsTest) getModule()).testAllTransactions(transactionID, getId());
    }
}
