package com.grendelscan.smashers.jobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByMimeTypeTest;

public class ByMimeTypeTestJob extends TransactionTestJob implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String mimeType;

    public ByMimeTypeTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final String mimeType)
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
