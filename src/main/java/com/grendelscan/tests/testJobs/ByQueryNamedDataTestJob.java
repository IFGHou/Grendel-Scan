/**
 * 
 */
package com.grendelscan.tests.testJobs;

import com.grendelscan.requester.http.dataHandling.containers.NamedDataContainer;
import com.grendelscan.scan.InterruptedScanException;
import com.grendelscan.tests.testModules.TestModule;
import com.grendelscan.tests.testTypes.ByQueryNamedDataTest;

/**
 * @author david
 *
 */
public class ByQueryNamedDataTestJob extends TransactionTestJob
{

	private static final long	serialVersionUID	= 1L;
	private NamedDataContainer datum;
	/**
	 * @param moduleClass
	 * @param transactionID
	 */
	public ByQueryNamedDataTestJob(Class<? extends TestModule> moduleClass, int transactionID, NamedDataContainer datum)
	{
		super(moduleClass, transactionID);
		this.datum = datum;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.tests.testJobs.TestJob#internalRunTest()
	 */
	@Override
	protected void internalRunTest() throws InterruptedScanException
	{
		((ByQueryNamedDataTest) getModule()).testByQueryNamedData(getTransactionID(), datum, getId());
	}

}
