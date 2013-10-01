package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.SessionIDTesting.SessionIDLocation;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.InitialAuthenticationTest;

public class InitialAuthenticationTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private SessionIDLocation	sessionIDLocation;

	public InitialAuthenticationTestJob(Class<? extends TestModule> moduleClass, int transactionID, SessionIDLocation sessionIDLocation)
	{
		super(moduleClass, transactionID);
		this.sessionIDLocation = sessionIDLocation;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((InitialAuthenticationTest) getModule()).testInitialAuthentication(transactionID, sessionIDLocation, this.getId());
	}
}
