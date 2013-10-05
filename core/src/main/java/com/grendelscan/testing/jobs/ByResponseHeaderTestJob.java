package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByResponseHeaderTest;

public class ByResponseHeaderTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final String ResponseHeader;

    public ByResponseHeaderTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final String responseHeader)
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
