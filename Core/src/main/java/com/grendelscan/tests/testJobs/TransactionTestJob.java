package com.grendelscan.tests.testJobs;

import com.grendelscan.logging.Log;
import com.grendelscan.scan.Scan;
import com.grendelscan.tests.testModules.TestModule;

public abstract class TransactionTestJob extends TestJob
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	protected int	transactionID;

	public TransactionTestJob(Class<? extends TestModule> moduleClass, int transactionID)
	{
		super(moduleClass);
		this.transactionID = transactionID;
		if (!Scan.getInstance().getTransactionRecord().getTransaction(transactionID).isSuccessfullExecution())
		{
			Log.warn("What???");
		}
	}

	public final int getTransactionID()
	{
		return transactionID;
	}

	public final void setTransactionID(int transactionID)
	{
		this.transactionID = transactionID;
	}

}
