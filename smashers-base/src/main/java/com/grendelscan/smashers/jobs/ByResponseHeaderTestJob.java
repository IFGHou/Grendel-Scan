package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByResponseHeaderTest;

public class ByResponseHeaderTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String ResponseHeader;

    public ByResponseHeaderTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final String responseHeader)
    {
        super(moduleClass, transactionID);
        ResponseHeader = responseHeader;
        this.transactionID = transactionID;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByResponseHeaderTest) getModule()).testByResponseHeader(transactionID, ResponseHeader, getId());
    }
}
