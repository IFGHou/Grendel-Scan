package com.grendelscan.tests.testJobs;

import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByRepeatableOutputContextTest;

public class ByRepeatableOutputContextTestJob extends TransactionTestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Collection<TokenContext> contexts;

	public ByRepeatableOutputContextTestJob(Class<? extends TestModule> moduleClass, int transactionID, Collection<TokenContext> contexts)
	{
		super(moduleClass, transactionID);
		this.contexts = contexts;
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByRepeatableOutputContextTest) getModule()).testByRepeatableOutputContext(contexts, this.getId());
	}
}
