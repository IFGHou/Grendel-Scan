package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByFileTest;

public class ByFileTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByFileTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByFileTest) getModule()).testByFile(transactionID, getId());
    }
}
