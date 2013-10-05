package com.grendelscan.testing.jobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByMimeTypeTest;

public class ByMimeTypeTestJob extends TransactionTestJob implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String mimeType;

    public ByMimeTypeTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final String mimeType)
    {
        super(moduleClass, transactionID);
        this.mimeType = mimeType;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByMimeTypeTest) getModule()).testByMimeType(transactionID, mimeType, getId());
    }
}
