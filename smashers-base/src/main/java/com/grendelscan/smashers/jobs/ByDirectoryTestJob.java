package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByDirectoryTest;

public class ByDirectoryTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String directory;

    public ByDirectoryTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final String directory)
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
