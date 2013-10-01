package com.grendelscan.tests.testJobs;

import java.io.Serializable;
import java.util.Collection;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.libraries.TokenTesting.TokenContext;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByOutputContextTest;

public class ByOutputContextTestJob extends TransactionTestJob implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Collection<TokenContext>	contexts;

	public ByOutputContextTestJob(Class<? extends TestModule> moduleClass, int transactionID, Collection<TokenContext> contexts)
	{
		super(moduleClass, transactionID);
		this.contexts = contexts;
		if (contexts == null || contexts.size() == 0)
		{
			throw new IllegalArgumentException("Contexts needs a value");
		}
	}

	@Override
	public void internalRunTest() throws InterruptedScanException
	{
		((ByOutputContextTest) getModule()).testByOutputContext(contexts, this.getId());
	}
}
