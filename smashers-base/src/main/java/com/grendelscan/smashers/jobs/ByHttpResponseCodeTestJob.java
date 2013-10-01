package com.grendelscan.smashers.jobs;

import java.io.Serializable;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.ByHttpResponseCodeTest;

public class ByHttpResponseCodeTestJob extends TransactionTestJob implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public ByHttpResponseCodeTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID)
    {
        super(moduleClass, transactionID);
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((ByHttpResponseCodeTest) getModule()).testByHttpResponseCode(transactionID, getId());
    }
}
