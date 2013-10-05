package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByDirectoryTest;

public class ByDirectoryTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String directory;

    public ByDirectoryTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final String directory)
    {
        super(moduleClass, transactionID);
        this.directory = directory;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByDirectoryTest) getModule()).testByDirectory(transactionID, directory, getId());
    }
}
