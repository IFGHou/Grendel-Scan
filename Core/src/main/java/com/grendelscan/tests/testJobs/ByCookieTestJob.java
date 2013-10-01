package com.grendelscan.tests.testJobs;

import org.apache.http.cookie.Cookie;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByCookieTest;

public class ByCookieTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Cookie	cookie;

	public ByCookieTestJob(Class<? extends TestModule> moduleClass, int transactionID, Cookie cookie)
	{
		super(moduleClass, transactionID);
		this.cookie = cookie;
		this.transactionID = transactionID;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByCookieTest) getModule()).testByCookie(transactionID, cookie, this.getId());
	}
}
