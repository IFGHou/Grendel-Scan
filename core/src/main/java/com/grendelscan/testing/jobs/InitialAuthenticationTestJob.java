package com.grendelscan.testing.jobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.testing.modules.AbstractTestModule;
import com.grendelscan.testing.modules.types.InitialAuthenticationTest;
import com.grendelscan.testing.utils.sessionIDs.SessionIDLocation;

public class InitialAuthenticationTestJob extends TransactionTestJob
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private final SessionIDLocation sessionIDLocation;

    public InitialAuthenticationTestJob(final Class<? extends AbstractTestModule> moduleClass, final int transactionID, final SessionIDLocation sessionIDLocation)
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
