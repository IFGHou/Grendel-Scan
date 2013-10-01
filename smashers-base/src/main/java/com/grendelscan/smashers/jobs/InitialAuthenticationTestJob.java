package com.grendelscan.smashers.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.smashers.AbstractSmasher;
import com.grendelscan.smashers.types.InitialAuthenticationTest;
import com.grendelscan.smashers.utils.sessionIDs.SessionIDLocation;

public class InitialAuthenticationTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final SessionIDLocation sessionIDLocation;

    public InitialAuthenticationTestJob(final Class<? extends AbstractSmasher> moduleClass, final int transactionID, final SessionIDLocation sessionIDLocation)
    {
        super(moduleClass, transactionID);
        this.sessionIDLocation = sessionIDLocation;
    }

    @Override
    public void internalRunTest() throws InterruptedScanException
    {
        ((InitialAuthenticationTest) getModule()).testInitialAuthentication(transactionID, sessionIDLocation, getId());
    }
}
