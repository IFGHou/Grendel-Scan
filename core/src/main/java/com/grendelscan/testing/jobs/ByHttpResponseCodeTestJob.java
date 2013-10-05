package com.grendelscan.testing.jobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.ByHttpResponseCodeTest;

public class ByHttpResponseCodeTestJob extends TransactionTestJob implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHttpResponseCodeTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHttpResponseCodeTest) getModule()).testByHttpResponseCode(transactionID, getId());
    }
}
