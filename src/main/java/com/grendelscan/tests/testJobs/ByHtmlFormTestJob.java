/**
 * 
 */
package com.grendelscan.tests.testJobs;

import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByHtmlFormTest;

/**
 * @author david
 *
 */
public class ByHtmlFormTestJob extends TransactionTestJob
{
	private static final long	serialVersionUID	= 1L;
	private String hash;
	/**
	 * @param moduleClass
	 * @param transactionID
	 */
	public ByHtmlFormTestJob(Class<? extends TestModule> moduleClass, int transactionID, String hash)
	{
		super(moduleClass, transactionID);
		this.hash = hash;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.tests.testJobs.TestJob#internalRunTest()
	 */
	@Override
	protected void internalRunTest() throws InterruptedScanException
	{
		((ByHtmlFormTest) getModule()).testByHtmlForm(transactionID, hash, getId());
	}

}
